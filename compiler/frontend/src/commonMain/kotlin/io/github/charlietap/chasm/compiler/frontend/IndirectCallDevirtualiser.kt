package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.declarations.ModuleTypeResolver
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.ValueType

internal typealias IndirectCallDevirtualiser = (Module, ModuleTypeResolver) -> Module

internal typealias OpenTables = (Module) -> BooleanArray?

internal typealias TableEntries = (Module, BooleanArray) -> Array<IntArray?>?

internal typealias FunctionDevirtualiser = (Function, ModuleTypeResolver, CallTargets) -> Function

internal fun IndirectCallDevirtualiser(
    module: Module,
    types: ModuleTypeResolver,
): Module = IndirectCallDevirtualiser(
    module = module,
    types = types,
    openTables = ::OpenTables,
    tableEntries = ::TableEntries,
    functionDevirtualiser = ::FunctionDevirtualiser,
)

internal inline fun IndirectCallDevirtualiser(
    module: Module,
    types: ModuleTypeResolver,
    crossinline openTables: OpenTables,
    crossinline tableEntries: TableEntries,
    crossinline functionDevirtualiser: FunctionDevirtualiser,
): Module {
    val open = openTables(module) ?: return module
    val tables = tableEntries(module, open) ?: return module
    val targets = CallTargets(tables, types)
    var changed = false
    val functions = module.functions.map { function ->
        val rewritten = functionDevirtualiser(function, types, targets)
        if (rewritten !== function) changed = true
        rewritten
    }
    return if (changed) module.copy(functions = functions) else module
}

internal fun OpenTables(
    module: Module,
): BooleanArray? {
    val imported = module.imports.count { import -> import.descriptor is Import.Descriptor.Table }
    val open = BooleanArray(imported + module.tables.size)
    open.fill(true, 0, imported)
    module.tables.forEachIndexed { index, table ->
        val type = table.type
        if (type.addressType != AddressType.I32 || type.limits.min > MAX_TABLE_ENTRIES.toULong()) open[imported + index] = true
    }
    module.exports.forEach { export ->
        val descriptor = export.descriptor
        if (descriptor is Export.Descriptor.Table) open[descriptor.tableIndex.toInt()] = true
    }
    if (open.all { it }) return null
    val called = BooleanArray(open.size)
    for (function in module.functions) {
        for (instruction in function.body.instructions) {
            when (instruction) {
                is ControlInstruction.CallIndirect -> called[instruction.tableIndex.toInt()] = true
                is TableInstruction.TableSet -> open[instruction.tableIdx.toInt()] = true
                is TableInstruction.TableGrow -> open[instruction.tableIdx.toInt()] = true
                is TableInstruction.TableFill -> open[instruction.tableIdx.toInt()] = true
                is TableInstruction.TableInit -> open[instruction.tableIdx.toInt()] = true
                is TableInstruction.TableCopy -> open[instruction.destTableIdx.toInt()] = true
                else -> Unit
            }
        }
    }
    for (table in open.indices) {
        if (!called[table]) open[table] = true
    }
    return if (open.all { it }) null else open
}

internal fun TableEntries(
    module: Module,
    open: BooleanArray,
): Array<IntArray?>? {
    val imported = open.size - module.tables.size
    val tables = arrayOfNulls<IntArray>(open.size)
    module.tables.forEachIndexed { index, table ->
        if (open[imported + index]) return@forEachIndexed
        tables[imported + index] = IntArray(table.type.limits.min.toInt()).also { entries -> entries.fill(entry(table.initExpression)) }
    }
    var certain = true
    for (segment in module.elementSegments) {
        val mode = segment.mode as? ElementSegment.Mode.Active ?: continue
        val table = mode.tableIndex.toInt()
        if (table < imported) {
            certain = false
            continue
        }
        val offset = (mode.offsetExpr.instructions.singleOrNull() as? NumericInstruction.I32Const)?.value
        if (offset == null) {
            tables[table] = null
            certain = false
            continue
        }
        val end = offset.toUInt().toULong() + segment.initExpressions.size.toULong()
        if (end > module.tables[table - imported].type.limits.min) {
            certain = false
            continue
        }
        val entries = tables[table] ?: continue
        segment.initExpressions.forEachIndexed { index, expression ->
            entries[offset + index] = if (certain) entry(expression) else NO_FUNCTION
        }
    }
    return if (tables.all { entries -> entries == null }) null else tables
}

private fun entry(
    expression: Expression,
): Int = (expression.instructions.singleOrNull() as? ReferenceInstruction.RefFunc)?.funcIdx?.toInt() ?: NO_FUNCTION

internal class Arms(
    val indices: IntArray,
    val functions: IntArray,
) {
    val size: Int
        get() = indices.size
}

internal class CallTargets(
    private val tables: Array<IntArray?>,
    private val types: ModuleTypeResolver,
) {
    private val heads = IntArray(tables.size) { NO_ENTRY }
    private var next = IntArray(CACHE_CAPACITY)
    private var keys = IntArray(CACHE_CAPACITY)
    private val cached = ArrayList<Arms>(CACHE_CAPACITY)

    fun at(
        call: ControlInstruction.CallIndirect,
    ): Arms {
        val table = call.tableIndex.toInt()
        val entries = tables[table] ?: return NO_ARMS
        if (types.functionType(call.typeIndex).results.types.size > 1) return NO_ARMS
        val type = call.typeIndex.toInt()
        var entry = heads[table]
        while (entry != NO_ENTRY) {
            if (keys[entry] == type) return cached[entry]
            entry = next[entry]
        }
        val matching = arms(entries, call.typeIndex)
        entry = cached.size
        if (entry == keys.size) {
            keys = keys.copyOf(entry * 2)
            next = next.copyOf(entry * 2)
        }
        keys[entry] = type
        next[entry] = heads[table]
        heads[table] = entry
        cached += matching
        return matching
    }

    private fun arms(
        entries: IntArray,
        type: Index.TypeIndex,
    ): Arms {
        var count = 0
        for (function in entries) {
            if (matches(function, type) && ++count > MAX_ARMS) return NO_ARMS
        }
        if (count == 0) return NO_ARMS
        val indices = IntArray(count)
        val functions = IntArray(count)
        var arm = 0
        entries.forEachIndexed { index, function ->
            if (!matches(function, type)) return@forEachIndexed
            indices[arm] = index
            functions[arm++] = function
        }
        return Arms(indices, functions)
    }

    private fun matches(
        function: Int,
        type: Index.TypeIndex,
    ): Boolean = function != NO_FUNCTION && types.functionTypeIndex(Index.FunctionIndex(function.toUInt())) == type
}

internal fun FunctionDevirtualiser(
    function: Function,
    types: ModuleTypeResolver,
    targets: CallTargets,
): Function {
    val body = function.body.instructions
    if (body.none { instruction -> instruction is ControlInstruction.CallIndirect && targets.at(instruction).size > 0 }) return function
    val locals = function.locals.toMutableList()
    val capture = OperandCapture(types.functionType(function.typeIndex).params.types.size, locals)
    val spills = Spills()
    val rewritten = ArrayList<Instruction>(body.size + 16)
    for (instruction in body) {
        val arms = (instruction as? ControlInstruction.CallIndirect)?.let(targets::at)
        if (arms == null || arms.size == 0) {
            rewritten += instruction
            continue
        }
        val type = types.functionType(instruction.typeIndex)
        val parameters = type.params.types
        val selector = parameters.size
        val operands = arrayOfNulls<Instruction>(selector + 1)
        var operand = capture.capture(rewritten, operands, 0)
        while (operand >= 0) {
            val value = if (operand == selector) I32_TYPE else parameters[operand]
            val local = when (val spilled = spills.local(operand, value)) {
                NO_LOCAL -> capture.local(value).also { local -> spills.add(operand, value, local) }
                else -> Index.LocalIndex(spilled.toUInt())
            }
            rewritten += VariableInstruction.LocalSet(local)
            operands[operand--] = VariableInstruction.LocalGet(local)
        }
        val arguments = operands.requireNoNulls()
        val block = type.results.types.singleOrNull()?.let(BlockType::ValType) ?: BlockType.Empty
        for (arm in 0 until arms.size) {
            rewritten += arguments[selector]
            rewritten += NumericInstruction.I32Const(arms.indices[arm])
            rewritten += NumericInstruction.I32Eq
            rewritten += ControlInstruction.If(block)
            for (argument in 0 until selector) rewritten += arguments[argument]
            rewritten += ControlInstruction.Call(Index.FunctionIndex(arms.functions[arm].toUInt()))
            rewritten += ControlInstruction.Else
        }
        for (argument in arguments) rewritten += argument
        rewritten += instruction
        rewritten += ControlInstruction.End(arms.size)
    }
    return function.copy(locals = locals, body = Expression(rewritten))
}

private class Spills {
    private var heads = IntArray(CACHE_CAPACITY) { NO_ENTRY }
    private var next = IntArray(CACHE_CAPACITY)
    private var locals = IntArray(CACHE_CAPACITY)
    private val types = ArrayList<ValueType>(CACHE_CAPACITY)

    fun local(
        operand: Int,
        type: ValueType,
    ): Int {
        if (operand >= heads.size) return NO_LOCAL
        var entry = heads[operand]
        while (entry != NO_ENTRY) {
            if (types[entry] == type) return locals[entry]
            entry = next[entry]
        }
        return NO_LOCAL
    }

    fun add(
        operand: Int,
        type: ValueType,
        local: Index.LocalIndex,
    ) {
        if (operand >= heads.size) {
            val size = heads.size
            heads = heads.copyOf(maxOf(size * 2, operand + 1))
            heads.fill(NO_ENTRY, size)
        }
        val entry = types.size
        if (entry == next.size) {
            next = next.copyOf(entry * 2)
            locals = locals.copyOf(entry * 2)
        }
        next[entry] = heads[operand]
        locals[entry] = local.idx.toInt()
        heads[operand] = entry
        types += type
    }
}

private val NO_ARMS = Arms(emptyIntArray, emptyIntArray)

private const val NO_FUNCTION = -1

private const val NO_ENTRY = -1

private const val NO_LOCAL = -1

private const val CACHE_CAPACITY = 8

private const val MAX_ARMS = 4

private const val MAX_TABLE_ENTRIES = 1 shl 20
