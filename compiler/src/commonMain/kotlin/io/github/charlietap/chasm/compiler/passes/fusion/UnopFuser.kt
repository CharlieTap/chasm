package io.github.charlietap.chasm.compiler.passes.fusion

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
    val destination = input.getOrNull(index + 1).let(destinationFactory)

    val instruction = when {
        operand == null && destination == FusedDestination.ValueStack -> instruction
        operand == null -> {
            fusedInstructionFactory(FusedOperand.ValueStack, destination)
        }
        else -> {
            output.removeLast()
            fusedInstructionFactory(operand, destination)
        }
    }

    output.add(instruction)

    if (destination != FusedDestination.ValueStack) {
        nextIndex++
    }

    return nextIndex
}
