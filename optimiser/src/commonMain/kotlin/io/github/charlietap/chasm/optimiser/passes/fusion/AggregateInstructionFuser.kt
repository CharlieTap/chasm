package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction

internal typealias AggregateInstructionFuser = (PassContext, Int, AggregateInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun AggregateInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: AggregateInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = AggregateInstructionFuser(
    context = context,
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    operandFactory = ::FusedOperandFactory,
    destinationFactory = ::FusedDestinationFactory,
)

internal inline fun AggregateInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: AggregateInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    operandFactory: FusedOperandFactory,
    destinationFactory: FusedDestinationFactory,
): Int = when (instruction) {
    is AggregateInstruction.StructGet -> {
        var nextIndex = index

        val address = input.getOrNull(index - 1)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (address == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                address == null -> FusedAggregateInstruction.StructGet(
                    address = FusedOperand.ValueStack,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                    fieldIndex = instruction.fieldIndex,
                )
                else -> {
                    output.removeLast()
                    FusedAggregateInstruction.StructGet(
                        address = address,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
                        fieldIndex = instruction.fieldIndex,
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
