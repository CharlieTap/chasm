package io.github.charlietap.chasm.compiler.backend.dex.analysis

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.NUMERIC_OPCODE_COUNT
import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.TableInstruction
import io.github.charlietap.chasm.cir.VariableInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.MemoryAccessTable
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation
import io.github.charlietap.chasm.compiler.backend.analysis.form.NumericForm
import io.github.charlietap.chasm.compiler.backend.analysis.form.form
import io.github.charlietap.chasm.compiler.backend.dex.ModuleLowering
import io.github.charlietap.chasm.type.NumberType

internal enum class CostFamily(
    val inline: Int,
    val helper: Int,
) {
    Constant(81, 23),
    Integer(28, 29),
    Float(37, 33),
    Convert(142, 152),
    Divide(13, 49),
    Wide(0, 0),
    Memory(136, 0),
    CheckedMemory(130, 0),
    HelperMemory(103, 104),
    BulkMemory(0, 0),
    Call(210, 116),
    Jump(33, 63),
    JumpTable(209, 82),
    Copy(96, 54),
    Select(58, 76),
    Global(183, 115),
    Reference(147, 507),
    Table(0, 0),
    Other(0, 0),
}

internal class CostFamilies {
    private val families = arrayOfNulls<CostFamily>(NUMERIC_OPCODE_COUNT)

    fun numeric(instruction: NumericInstruction): CostFamily {
        return families[instruction.opcode] ?: numericFamily(instruction).also { family -> families[instruction.opcode] = family }
    }
}

internal class DexSizeEstimate(
    val inline: Int,
    val helper: Int,
    private val prefix: LongArray,
    private val instructions: Int,
) {
    fun reach(
        start: Int,
        budget: Int,
    ): Int {

        val limit = prefix[start] + budget.toLong() * WEIGHT_SCALE
        var low = start + 1
        var high = instructions

        while (low < high) {
            val middle = (low + high + 1) ushr 1
            if (prefix[middle] <= limit) {
                low = middle
            } else {
                high = middle - 1
            }
        }

        return low
    }
}

internal fun estimateDexSize(
    module: ModuleLowering,
    function: CirFunction,
    analysis: FunctionAnalysis,
): DexSizeEstimate {

    val instructions = function.instructions
    val prefix = module.workspace.estimatePrefix(instructions.size + 1)
    prefix[0] = 0L
    var helper = 0L
    for (position in instructions.indices) {

        val instruction = instructions[position]
        val family = costFamily(module, analysis, instruction, position)
        val helperFamily = if (family == CostFamily.Memory || family == CostFamily.CheckedMemory) CostFamily.HelperMemory else family
        val targets = if (instruction is AdminInstruction.JumpTableS) instruction.targets.size else 0
        val transfers = transfers(instruction)

        prefix[position + 1] = prefix[position] + family.inline + INLINE_TARGET * targets + INLINE_TRANSFER * transfers
        helper += helperFamily.helper + HELPER_TARGET * targets + HELPER_TRANSFER * transfers
    }

    val frameSlots = analysis.facts.frameSlots
    val inline = prefix[instructions.size] + INLINE_FRAME_SLOT * frameSlots + INLINE_CONSTANT
    val helperTotal = helper + HELPER_FRAME_SLOT * frameSlots + HELPER_CONSTANT

    return DexSizeEstimate(estimatedUnits(inline), estimatedUnits(helperTotal), prefix, instructions.size)
}

private fun estimatedUnits(weighted: Long): Int {
    return minOf(weighted / WEIGHT_SCALE, Int.MAX_VALUE.toLong()).toInt()
}

private fun costFamily(
    module: ModuleLowering,
    analysis: FunctionAnalysis,
    instruction: Instruction,
    position: Int,
): CostFamily {
    return when (instruction) {
        is NumericInstruction -> module.costFamilies.numeric(instruction)
        is MemoryInstruction -> memoryFamily(module, analysis, position)
        is AdminInstruction -> when (instruction) {
            is AdminInstruction.CopySlot, is AdminInstruction.CopySlots, is AdminInstruction.CopySlotSequence -> CostFamily.Copy
            is AdminInstruction.JumpTableS -> CostFamily.JumpTable
            is AdminInstruction.EndFunction, AdminInstruction.Checkpoint -> CostFamily.Other
            else -> CostFamily.Jump
        }
        is ControlInstruction -> when (instruction) {
            is ControlInstruction.Unreachable, is ControlInstruction.FunctionReturn -> CostFamily.Other
            else -> CostFamily.Call
        }
        is ParametricInstruction -> CostFamily.Select
        is VariableInstruction -> CostFamily.Global
        is AggregateInstruction, is ReferenceInstruction -> CostFamily.Reference
        is TableInstruction -> CostFamily.Table
    }
}

private fun transfers(instruction: Instruction): Int {
    return when (instruction) {
        is AdminInstruction.CopySlots -> instruction.sourceSlots.size
        is AdminInstruction.CopySlotSequence -> instruction.sourceSlots.size
        is AdminInstruction.JumpCopies -> instruction.operands.values.size
        is ControlInstruction.Call -> instruction.operands.values.size
        is ControlInstruction.ReturnCall -> instruction.operands.wasm.size
        else -> 0
    }
}

private fun numericFamily(instruction: NumericInstruction): CostFamily {
    return when (val form = instruction.form()) {
        is NumericForm.Constant -> CostFamily.Constant
        is NumericForm.BitFieldExtract -> CostFamily.Integer
        is NumericForm.Convert -> CostFamily.Convert
        is NumericForm.Wide -> CostFamily.Wide
        is NumericForm.Binary -> when {
            floating(form.type) -> CostFamily.Float
            form.operation == BinaryOperation.DivS || form.operation == BinaryOperation.DivU ||
                form.operation == BinaryOperation.RemS || form.operation == BinaryOperation.RemU -> CostFamily.Divide
            else -> CostFamily.Integer
        }
        is NumericForm.Unary -> if (floating(form.type)) CostFamily.Float else CostFamily.Integer
        is NumericForm.Compare -> if (floating(form.type)) CostFamily.Float else CostFamily.Integer
    }
}

private fun floating(type: NumberType): Boolean {
    return type == NumberType.F32 || type == NumberType.F64
}

private fun memoryFamily(
    module: ModuleLowering,
    analysis: FunctionAnalysis,
    position: Int,
): CostFamily {

    val table = analysis.facts.table
    val access = table.accesses.accessIndexAt(position)
    if (access < 0) {
        return CostFamily.BulkMemory
    }

    val inlineMemory = module.inlineMemory
    if (!analysis.usesInlineMemory || inlineMemory == null || table.accesses.memories[access] != inlineMemory.index) {
        return CostFamily.HelperMemory
    }
    if (analysis.checkedAccesses[position] || table.accesses.addresses[access] == MemoryAccessTable.CONSTANT_ADDRESS) {
        return CostFamily.CheckedMemory
    }

    return CostFamily.Memory
}

private const val WEIGHT_SCALE = 10

private const val INLINE_TARGET = 20

private const val INLINE_TRANSFER = 3

private const val INLINE_FRAME_SLOT = 41

private const val INLINE_CONSTANT = 189

private const val HELPER_TARGET = 23

private const val HELPER_TRANSFER = 5

private const val HELPER_FRAME_SLOT = 14

private const val HELPER_CONSTANT = 412
