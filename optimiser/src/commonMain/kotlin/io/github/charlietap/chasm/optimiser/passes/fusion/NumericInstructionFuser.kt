package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Abs
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32DivS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32DivU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Mul
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Sub
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction

internal typealias NumericInstructionFuser = (Int, NumericInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun NumericInstructionFuser(
    index: Int,
    instruction: NumericInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = NumericInstructionFuser(
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    unop = ::UnopFuser,
    binop = ::BinopFuser,
)

internal inline fun NumericInstructionFuser(
    index: Int,
    instruction: NumericInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    unop: UnopFuser,
    binop: BinopFuser,
): Int = when (instruction) {
    is NumericInstruction.I32Add -> binop(index, instruction, input, output, ::I32Add)
    is NumericInstruction.I32Sub -> binop(index, instruction, input, output, ::I32Sub)
    is NumericInstruction.I32Mul -> binop(index, instruction, input, output, ::I32Mul)
    is NumericInstruction.I32DivS -> binop(index, instruction, input, output, ::I32DivS)
    is NumericInstruction.I32DivU -> binop(index, instruction, input, output, ::I32DivU)
    is NumericInstruction.F32Abs -> unop(index, instruction, input, output, ::F32Abs)
    else -> {
        output.add(instruction)
        index
    }
}
