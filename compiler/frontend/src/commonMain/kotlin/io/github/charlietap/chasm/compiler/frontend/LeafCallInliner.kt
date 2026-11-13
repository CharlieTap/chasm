package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.declarations.ModuleTypeResolver
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType

internal class LeafCallInliner(
    private val leafAnalyzer: LeafAnalyzer,
    private val functionInliner: FunctionInliner,
) {
    fun rewrite(
        module: Module,
        types: ModuleTypeResolver,
    ): Module {

        val analyzed = module.functions.map { function -> leafAnalyzer.analyze(function, types) }
        if (analyzed.all { leaf -> leaf == null }) return module

        val imported = module.imports.count { import -> import.descriptor is Import.Descriptor.Function }
        val leaves = LeavesByFunction(imported, analyzed)
        var changed = false
        val functions = module.functions.map { function ->
            val inlined = functionInliner.inline(function, types, leaves)
            if (inlined !== function) changed = true
            inlined
        }

        return if (changed) module.copy(functions = functions) else module
    }
}

internal class LeavesByFunction(
    private val imported: Int,
    private val leaves: List<Leaf?>,
) {
    operator fun get(function: Int): Leaf? {
        return leaves.getOrNull(function - imported)
    }
}

internal data class Leaf(
    val parameters: Int,
    val firstReadOnlyParameter: Int,
    val localTypes: List<ValueType>,
    val result: ValueType?,
    val body: List<Instruction>,
    val readLocals: BooleanArray,
    val writtenLocals: BooleanArray,
    val localsNeedingDefault: BooleanArray,
    val exitsEarly: Boolean,
) {
    val inlinedSize: Int = parameters + 2 * localsNeedingDefault.count { needed -> needed } + body.size + 2
}

internal class LeafAnalyzer {

    fun analyze(
        function: Function,
        types: ModuleTypeResolver,
    ): Leaf? {

        val type = types.functionType(function.typeIndex)
        val results = type.results.types
        val body = function.body.instructions
        if (results.size > 1 || body.size > MAX_LEAF_INSTRUCTIONS) return null
        if (body.any(Instruction::preventsInlining)) return null

        val parameters = type.params.types
        if (parameters.size + function.locals.size > MAX_LEAF_LOCALS) return null
        val locals = parameters + function.locals.map(Local::type)
        if (locals.any { value -> value !is ValueType.Number && value !is ValueType.Reference }) return null

        val readLocals = BooleanArray(locals.size)
        val writtenLocals = BooleanArray(locals.size)
        val initialised = BooleanArray(locals.size) { index -> index < parameters.size }
        val localsNeedingDefault = BooleanArray(locals.size)
        var exitsEarly = false
        var depth = 0
        var beforeControlFlow = true

        for (instruction in body) {
            when (instruction) {
                is VariableInstruction.LocalGet -> {
                    val index = instruction.localIdx.toInt()
                    if (!initialised[index]) localsNeedingDefault[index] = locals[index].isDefaultable()
                    readLocals[index] = true
                }
                is VariableInstruction.LocalSet -> {
                    val index = instruction.localIdx.toInt()
                    writtenLocals[index] = true
                    if (beforeControlFlow && !readLocals[index]) initialised[index] = true
                }
                is VariableInstruction.LocalTee -> {
                    val index = instruction.localIdx.toInt()
                    writtenLocals[index] = true
                    if (beforeControlFlow && !readLocals[index]) initialised[index] = true
                }
                is ControlInstruction -> {
                    beforeControlFlow = false
                    if (instruction.exitsFunction(depth)) exitsEarly = true
                    depth += instruction.blockDepthChange()
                }
                else -> Unit
            }
        }

        var firstReadOnlyParameter = parameters.size
        while (firstReadOnlyParameter > 0 && !writtenLocals[firstReadOnlyParameter - 1]) firstReadOnlyParameter--

        return Leaf(
            parameters = parameters.size,
            firstReadOnlyParameter = firstReadOnlyParameter,
            localTypes = locals,
            result = results.singleOrNull(),
            body = body,
            readLocals = readLocals,
            writtenLocals = writtenLocals,
            localsNeedingDefault = localsNeedingDefault,
            exitsEarly = exitsEarly,
        )
    }
}

internal class FunctionInliner {

    fun inline(
        function: Function,
        types: ModuleTypeResolver,
        leaves: LeavesByFunction,
    ): Function {

        val body = function.body.instructions
        if (body.none { instruction -> instruction is ControlInstruction.Call && leaves[instruction.functionIndex.toInt()] != null }) return function

        val locals = function.locals.toMutableList()
        val expander = LeafExpander(OperandCapture(types.functionType(function.typeIndex).params.types.size, locals))
        var budget = body.size.coerceIn(MIN_INLINING_BUDGET, MAX_INLINING_BUDGET)
        val inlined = ArrayList<Instruction>(minOf(body.size * 2, body.size + budget))
        var inlinedAny = false

        for (instruction in body) {

            val callee = (instruction as? ControlInstruction.Call)?.functionIndex?.toInt()
            val leaf = callee?.let(leaves::get)
            if (leaf == null || leaf.inlinedSize > budget) {
                inlined += instruction
                continue
            }

            budget -= leaf.inlinedSize
            inlinedAny = true
            expander.expand(leaf, callee, inlined)
        }

        return if (inlinedAny) function.copy(locals = locals, body = Expression(inlined)) else function
    }
}

private class LeafExpander(
    private val capture: OperandCapture,
) {
    private val calleeLocals = HashMap<Int, IntArray>()

    fun expand(
        leaf: Leaf,
        callee: Int,
        into: MutableList<Instruction>,
    ) {

        val localIndices = calleeLocals.getOrPut(callee) { IntArray(leaf.localTypes.size) { _ -> UNASSIGNED } }
        val arguments = arrayOfNulls<Instruction>(leaf.parameters)
        var parameter = capture.capture(into, arguments, leaf.firstReadOnlyParameter)

        while (parameter >= 0) {
            into += if (leaf.readLocals[parameter] || leaf.writtenLocals[parameter]) {
                VariableInstruction.LocalSet(callerLocal(localIndices, leaf, parameter))
            } else {
                ParametricInstruction.Drop
            }
            parameter--
        }

        for (index in leaf.parameters until leaf.localTypes.size) {
            if (!leaf.localsNeedingDefault[index]) continue
            into += defaultValue(leaf.localTypes[index])
            into += VariableInstruction.LocalSet(callerLocal(localIndices, leaf, index))
        }

        if (leaf.exitsEarly) into += ControlInstruction.Block(leaf.result?.let(BlockType::ValType) ?: BlockType.Empty)

        var depth = 0
        for (instruction in leaf.body) {

            into += when (instruction) {
                is VariableInstruction.LocalGet -> {
                    val index = instruction.localIdx.toInt()
                    arguments.getOrNull(index) ?: VariableInstruction.LocalGet(callerLocal(localIndices, leaf, index))
                }
                is VariableInstruction.LocalSet -> VariableInstruction.LocalSet(callerLocal(localIndices, leaf, instruction.localIdx.toInt()))
                is VariableInstruction.LocalTee -> VariableInstruction.LocalTee(callerLocal(localIndices, leaf, instruction.localIdx.toInt()))
                ControlInstruction.Return -> ControlInstruction.Br(Index.LabelIndex(depth.toUInt()))
                else -> instruction
            }

            depth += instruction.blockDepthChange()
        }

        if (leaf.exitsEarly) into += ControlInstruction.End(1)
    }

    private fun callerLocal(
        localIndices: IntArray,
        leaf: Leaf,
        index: Int,
    ): Index.LocalIndex {

        if (localIndices[index] == UNASSIGNED) localIndices[index] = capture.local(leaf.localTypes[index]).toInt()

        return Index.LocalIndex(localIndices[index].toUInt())
    }
}

private fun Instruction.preventsInlining(): Boolean {
    return when (this) {
        is ControlInstruction.Call,
        is ControlInstruction.CallIndirect,
        is ControlInstruction.CallRef,
        is ControlInstruction.ReturnCall,
        is ControlInstruction.ReturnCallIndirect,
        is ControlInstruction.ReturnCallRef,
        is ControlInstruction.Loop,
        is ControlInstruction.TryTable,
        -> true
        else -> false
    }
}

private fun ValueType.isDefaultable(): Boolean {
    return this is ValueType.Number || (this is ValueType.Reference && referenceType is ReferenceType.RefNull)
}

private fun ControlInstruction.exitsFunction(depth: Int): Boolean {
    return when (this) {
        ControlInstruction.Return -> true
        is ControlInstruction.Br -> labelIndex.idx.toInt() >= depth
        is ControlInstruction.BrIf -> labelIndex.idx.toInt() >= depth
        is ControlInstruction.BrOnNull -> labelIndex.idx.toInt() >= depth
        is ControlInstruction.BrOnNonNull -> labelIndex.idx.toInt() >= depth
        is ControlInstruction.BrOnCast -> labelIndex.idx.toInt() >= depth
        is ControlInstruction.BrOnCastFail -> labelIndex.idx.toInt() >= depth
        is ControlInstruction.BrTable -> defaultLabelIndex.idx.toInt() >= depth || labelIndices.any { label -> label.idx.toInt() >= depth }
        else -> false
    }
}

private inline fun Instruction.blockDepthChange(): Int {
    return when (this) {
        is ControlInstruction.Block, is ControlInstruction.If -> 1
        is ControlInstruction.End -> -count
        else -> 0
    }
}

private fun defaultValue(type: ValueType): Instruction {
    return when (type) {
        is ValueType.Number -> when (type.numberType) {
            NumberType.I32 -> NumericInstruction.I32Const(0)
            NumberType.I64 -> NumericInstruction.I64Const(0L)
            NumberType.F32 -> NumericInstruction.F32Const(0f, 0)
            NumberType.F64 -> NumericInstruction.F64Const(0.0, 0L)
        }
        is ValueType.Reference -> ReferenceInstruction.RefNull(type.referenceType.heapType)
        else -> error("$type has no inlinable default")
    }
}

private const val MAX_LEAF_INSTRUCTIONS = 30

private const val MAX_LEAF_LOCALS = 16

private const val MIN_INLINING_BUDGET = 256

private const val MAX_INLINING_BUDGET = 4_096

private const val UNASSIGNED = -1
