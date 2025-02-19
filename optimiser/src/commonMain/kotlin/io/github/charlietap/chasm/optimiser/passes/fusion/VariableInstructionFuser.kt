package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedVariableInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.VariableInstruction

internal typealias VariableInstructionFuser = (Int, VariableInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun VariableInstructionFuser(
    index: Int,
    instruction: VariableInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = VariableInstructionFuser(
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    operandFactory = ::FusedOperandFactory,
)

internal inline fun VariableInstructionFuser(
    index: Int,
    instruction: VariableInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    operandFactory: FusedOperandFactory,
): Int = when (instruction) {
    is VariableInstruction.GlobalSet -> {
        val operand = input.getOrNull(index - 1)?.let(operandFactory)
        if (operand == null) {
            output.add(instruction)
        } else {
            output.removeLast()
            output.add(
                FusedVariableInstruction.GlobalSet(
                    operand = operand,
                    globalIdx = instruction.globalIdx,
                ),
            )
        }

        index
    }
    is VariableInstruction.LocalSet -> {
        val operand = input.getOrNull(index - 1)?.let(operandFactory)
        if (operand == null) {
            output.add(instruction)
        } else {
            output.removeLast()
            output.add(
                FusedVariableInstruction.LocalSet(
                    operand = operand,
                    localIdx = instruction.localIdx,
                ),
            )
        }

        index
    }
    is VariableInstruction.LocalTee -> {
        val operand = input.getOrNull(index - 1)?.let(operandFactory)
        if (operand == null) {
            output.add(instruction)
        } else {
            output.removeLast()
            output.add(
                FusedVariableInstruction.LocalTee(
                    operand = operand,
                    localIdx = instruction.localIdx,
                ),
            )
        }

        index
    }
    else -> {
        output.add(instruction)
        index
    }
}
