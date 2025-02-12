package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction

internal typealias BinopFusedInstructionFactory = (FusedOperand, FusedOperand, FusedDestination) -> Instruction
internal typealias BinopFuser = (Int, Instruction, List<Instruction>, MutableList<Instruction>, BinopFusedInstructionFactory) -> Int

internal inline fun BinopFuser(
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
    val left = input.getOrNull(index - 2)?.let(operandFactory)
    val right = input.getOrNull(index - 1)?.let(operandFactory)

    if (left == null || right == null) {
        output.add(instruction)
        return index + 1
    }

    output.removeLast()
    output.removeLast()

    val destination = input.getOrNull(index + 1).let(destinationFactory)

    if (destination != FusedDestination.ValueStack) {
        nextIndex++
    }

    output.add(
        fusedInstructionFactory(left, right, destination),
    )

    return nextIndex
}
