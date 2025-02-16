package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Abs
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Add
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction

internal typealias InstructionFuser = (Int, Instruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun InstructionFuser(
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = InstructionFuser(
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    unop = ::UnopFuser,
    binop = ::BinopFuser,
)

internal inline fun InstructionFuser(
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    unop: UnopFuser,
    binop: BinopFuser,
): Int = when (instruction) {
    is NumericInstruction.I32Add -> binop(index, instruction, input, output, ::I32Add)
    is NumericInstruction.F32Abs -> unop(index, instruction, input, output, ::F32Abs)
    else -> {
        output.add(instruction)
        index
    }
}
