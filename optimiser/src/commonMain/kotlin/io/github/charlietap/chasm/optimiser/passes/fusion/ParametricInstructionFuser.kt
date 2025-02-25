package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.FusedParametricInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.ParametricInstruction

internal typealias ParametricInstructionFuser = (PassContext, Int, ParametricInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun ParametricInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: ParametricInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = ParametricInstructionFuser(
    context = context,
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    operandFactory = ::FusedOperandFactory,
    destinationFactory = ::FusedDestinationFactory,
)

internal inline fun ParametricInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: ParametricInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    operandFactory: FusedOperandFactory,
    destinationFactory: FusedDestinationFactory,
): Int = when (instruction) {
    is ParametricInstruction.Select,
    is ParametricInstruction.SelectWithType,
    -> {
        var nextIndex = index

        val const = input.getOrNull(index - 1)?.let(operandFactory)
        val val2 = input.getOrNull(index - 2)?.let(operandFactory)
        val val1 = input.getOrNull(index - 3)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (const == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                const == null -> FusedParametricInstruction.Select(
                    const = FusedOperand.ValueStack,
                    val1 = FusedOperand.ValueStack,
                    val2 = FusedOperand.ValueStack,
                    destination = destination,
                )
                val2 == null -> {
                    output.removeLast()
                    FusedParametricInstruction.Select(
                        const = const,
                        val1 = FusedOperand.ValueStack,
                        val2 = FusedOperand.ValueStack,
                        destination = destination,
                    )
                }
                val1 == null -> {
                    output.removeLast()
                    output.removeLast()
                    FusedParametricInstruction.Select(
                        const = const,
                        val1 = FusedOperand.ValueStack,
                        val2 = val2,
                        destination = destination,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    output.removeLast()
                    FusedParametricInstruction.Select(
                        const = const,
                        val1 = val1,
                        val2 = val2,
                        destination = destination,
                    )
                }
            }
        }

        output.add(instruction)

        if (destination != FusedDestination.ValueStack) {
            nextIndex++
        }

        nextIndex
    }
    else -> {
        output.add(instruction)
        index
    }
}
