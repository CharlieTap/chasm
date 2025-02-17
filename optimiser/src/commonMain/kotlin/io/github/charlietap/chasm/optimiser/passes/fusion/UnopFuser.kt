package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction

internal typealias UnopFusedInstructionFactory = (FusedOperand, FusedDestination) -> Instruction
internal typealias UnopFuser = (Int, Instruction, List<Instruction>, MutableList<Instruction>, UnopFusedInstructionFactory) -> Int

internal fun UnopFuser(
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    fusedInstructionFactory: UnopFusedInstructionFactory,
): Int = UnopFuser(
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    fusedInstructionFactory = fusedInstructionFactory,
    operandFactory = ::FusedOperandFactory,
    destinationFactory = ::FusedDestinationFactory,
)

internal inline fun UnopFuser(
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    fusedInstructionFactory: UnopFusedInstructionFactory,
    operandFactory: FusedOperandFactory,
    destinationFactory: FusedDestinationFactory,
): Int {
    var nextIndex = index
    val operand = input.getOrNull(index - 1)?.let(operandFactory)

    if (operand == null) {
        output.add(instruction)
        return index
    }

    output.removeLast()

    val destination = input.getOrNull(index + 1).let(destinationFactory)

    if (destination != FusedDestination.ValueStack) {
        nextIndex++
    }

    output.add(
        fusedInstructionFactory(operand, destination),
    )

    return nextIndex
}
