package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction

internal fun CommutativeBinopFuser(
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    fusedInstructionFactory: BinopFusedInstructionFactory,
): Int = CommutativeBinopFuser(
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    fusedInstructionFactory = fusedInstructionFactory,
    operandFactory = ::FusedOperandFactory,
    destinationFactory = ::FusedDestinationFactory,
)

internal inline fun CommutativeBinopFuser(
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    fusedInstructionFactory: BinopFusedInstructionFactory,
    operandFactory: FusedOperandFactory,
    destinationFactory: FusedDestinationFactory,
): Int {

    var nextIndex = index

    val right = input.getOrNull(index - 1)?.let(operandFactory)
    val left = input.getOrNull(index - 2)?.let(operandFactory)
    val destination = input.getOrNull(index + 1).let(destinationFactory)

    val instruction = when {
        right == null && destination == FusedDestination.ValueStack -> instruction
        right == null -> fusedInstructionFactory(
            FusedOperand.ValueStack,
            FusedOperand.ValueStack,
            destination,
        )
        left == null -> {
            output.removeAt(output.lastIndex)
            fusedInstructionFactory(
                FusedOperand.ValueStack,
                right,
                destination,
            )
        }
        else -> {
            output.removeAt(output.lastIndex)
            output.removeAt(output.lastIndex)
            fusedInstructionFactory(left, right, destination)
        }
    }

    output.add(instruction)

    if (right != null && destination != FusedDestination.ValueStack) {
        nextIndex++
    }

    return nextIndex
}
