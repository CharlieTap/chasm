package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction

internal typealias BinopFusedInstructionFactory = (FusedOperand, FusedOperand, FusedDestination) -> Instruction
internal typealias BinopFuser = (Int, Instruction, List<Instruction>, MutableList<Instruction>, BinopFusedInstructionFactory) -> Int

internal fun BinopFuser(
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    fusedInstructionFactory: BinopFusedInstructionFactory,
): Int = BinopFuser(
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    fusedInstructionFactory = fusedInstructionFactory,
    operandFactory = ::FusedOperandFactory,
    destinationFactory = ::FusedDestinationFactory,
)

internal inline fun BinopFuser(
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
        right == null -> instruction
        left == null -> {
            output.removeLast()
            fusedInstructionFactory(FusedOperand.ValueStack, right, destination)
        }
        else -> {
            output.removeLast()
            output.removeLast()
            fusedInstructionFactory(left, right, destination)
        }
    }

    output.add(instruction)

    if (right != null && destination != FusedDestination.ValueStack) {
        nextIndex++
    }

    return nextIndex
}
