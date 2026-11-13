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
        val leaves = Leaves(imported, analyzed)
        var changed = false
        val functions = module.functions.map { function ->
            val inlined = functionInliner.inline(function, types, leaves)
            if (inlined !== function) changed = true
            inlined
        }

        return if (changed) module.copy(functions = functions) else module
    }
}

internal class Leaves(
    private val imported: Int,
    private val leaves: List<Leaf?>,
) {
    operator fun get(function: Int): Leaf? {
        return leaves.getOrNull(function - imported)
    }
}

internal data class Leaf(
    val parameters: Int,
    val unwrittenFrom: Int,
    val localTypes: List<ValueType>,
    val result: ValueType?,
    val body: List<Instruction>,
    val read: BooleanArray,
    val written: BooleanArray,
    val needsDefault: BooleanArray,
    val exitsBody: Boolean,
) {
    val cost: Int = parameters + 2 * needsDefault.count { needed -> needed } + body.size + 2
}

internal class LeafAnalyzer {

    fun analyze(
        function: Function,
        types: ModuleTypeResolver,
    ): Leaf? {

        val type = types.functionType(function.typeIndex)
        val results = type.results.types
        val body = function.body.instructions
        if (results.size > 1 || body.size > LEAF_INSTRUCTIONS) return null
        if (body.any(Instruction::preventsInlining)) return null

        val parameters = type.params.types
        if (parameters.size + function.locals.size > LEAF_LOCALS) return null
        val locals = parameters + function.locals.map(Local::type)
        if (locals.any { value -> value !is ValueType.Number && value !is ValueType.Reference }) return null

        val read = BooleanArray(locals.size)
        val written = BooleanArray(locals.size)
        val initialised = BooleanArray(locals.size) { index -> index < parameters.size }
        val needsDefault = BooleanArray(locals.size)
        var exitsBody = false
        var depth = 0
        var straightLine = true

        for (instruction in body) {
            when (instruction) {
                is VariableInstruction.LocalGet -> {
                    val index = instruction.localIdx.toInt()
                    if (!initialised[index]) needsDefault[index] = locals[index].isDefaultable()
                    read[index] = true
                }
                is VariableInstruction.LocalSet -> {
                    val index = instruction.localIdx.toInt()
                    written[index] = true
                    if (straightLine && !read[index]) initialised[index] = true
                }
                is VariableInstruction.LocalTee -> {
                    val index = instruction.localIdx.toInt()
                    written[index] = true
                    if (straightLine && !read[index]) initialised[index] = true
                }
                is ControlInstruction -> {
                    straightLine = false
                    if (instruction.exits(depth)) exitsBody = true
                    depth += instruction.depthChange()
                }
                else -> Unit
            }
        }

        var unwrittenFrom = parameters.size
        while (unwrittenFrom > 0 && !written[unwrittenFrom - 1]) unwrittenFrom--

        return Leaf(
            parameters = parameters.size,
            unwrittenFrom = unwrittenFrom,
            localTypes = locals,
            result = results.singleOrNull(),
            body = body,
            read = read,
            written = written,
            needsDefault = needsDefault,
            exitsBody = exitsBody,
        )
    }
}

internal class FunctionInliner {

    fun inline(
        function: Function,
        types: ModuleTypeResolver,
        leaves: Leaves,
    ): Function {

        val body = function.body.instructions
        if (body.none { instruction -> instruction is ControlInstruction.Call && leaves[instruction.functionIndex.toInt()] != null }) return function

        val locals = function.locals.toMutableList()
        val expander = LeafExpander(OperandCapture(types.functionType(function.typeIndex).params.types.size, locals))
        var budget = body.size.coerceIn(MIN_INLINED_INSTRUCTIONS, MAX_INLINED_INSTRUCTIONS)
        val inlined = ArrayList<Instruction>(minOf(body.size * 2, body.size + budget))
        var expanded = false

        for (instruction in body) {

            val callee = (instruction as? ControlInstruction.Call)?.functionIndex?.toInt()
            val leaf = callee?.let(leaves::get)
            if (leaf == null || leaf.cost > budget) {
                inlined += instruction
                continue
            }

            budget -= leaf.cost
            expanded = true
            expander.expand(leaf, callee, inlined)
        }

        return if (expanded) function.copy(locals = locals, body = Expression(inlined)) else function
    }
}

private class LeafExpander(
    private val capture: OperandCapture,
) {
    private val assigned = HashMap<Int, IntArray>()

    fun expand(
        leaf: Leaf,
        callee: Int,
        into: MutableList<Instruction>,
    ) {

        val indices = assigned.getOrPut(callee) { IntArray(leaf.localTypes.size) { _ -> UNASSIGNED } }
        val arguments = arrayOfNulls<Instruction>(leaf.parameters)
        var parameter = capture.capture(into, arguments, leaf.unwrittenFrom)

        while (parameter >= 0) {
            into += if (leaf.read[parameter] || leaf.written[parameter]) {
                VariableInstruction.LocalSet(local(indices, leaf, parameter))
            } else {
                ParametricInstruction.Drop
            }
            parameter--
        }

        for (index in leaf.parameters until leaf.localTypes.size) {
            if (!leaf.needsDefault[index]) continue
            into += defaultValue(leaf.localTypes[index])
            into += VariableInstruction.LocalSet(local(indices, leaf, index))
        }

        if (leaf.exitsBody) into += ControlInstruction.Block(leaf.result?.let(BlockType::ValType) ?: BlockType.Empty)

        var depth = 0
        for (instruction in leaf.body) {

            into += when (instruction) {
                is VariableInstruction.LocalGet -> {
                    val index = instruction.localIdx.toInt()
                    arguments.getOrNull(index) ?: VariableInstruction.LocalGet(local(indices, leaf, index))
                }
                is VariableInstruction.LocalSet -> VariableInstruction.LocalSet(local(indices, leaf, instruction.localIdx.toInt()))
                is VariableInstruction.LocalTee -> VariableInstruction.LocalTee(local(indices, leaf, instruction.localIdx.toInt()))
                ControlInstruction.Return -> ControlInstruction.Br(Index.LabelIndex(depth.toUInt()))
                else -> instruction
            }

            depth += instruction.depthChange()
        }

        if (leaf.exitsBody) into += ControlInstruction.End(1)
    }

    private fun local(
        indices: IntArray,
        leaf: Leaf,
        index: Int,
    ): Index.LocalIndex {

        if (indices[index] == UNASSIGNED) indices[index] = capture.local(leaf.localTypes[index]).toInt()

        return Index.LocalIndex(indices[index].toUInt())
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

private fun ControlInstruction.exits(depth: Int): Boolean {
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

private inline fun Instruction.depthChange(): Int {
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

private const val LEAF_INSTRUCTIONS = 30

private const val LEAF_LOCALS = 16

private const val MIN_INLINED_INSTRUCTIONS = 256

private const val MAX_INLINED_INSTRUCTIONS = 4_096

private const val UNASSIGNED = -1
