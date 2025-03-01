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
    is AggregateInstruction.ArrayGet -> {
        var nextIndex = index

        val field = input.getOrNull(index - 1)?.let(operandFactory)
        val address = input.getOrNull(index - 2)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (field == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                field == null -> FusedAggregateInstruction.ArrayGet(
                    field = FusedOperand.ValueStack,
                    address = FusedOperand.ValueStack,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                )
                address == null -> {
                    output.removeLast()
                    FusedAggregateInstruction.ArrayGet(
                        field = field,
                        address = FusedOperand.ValueStack,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    FusedAggregateInstruction.ArrayGet(
                        field = field,
                        address = address,
                        destination = destination,
                        typeIndex = instruction.typeIndex,
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
    is AggregateInstruction.StructGetSigned -> {
        var nextIndex = index

        val address = input.getOrNull(index - 1)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (address == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                address == null -> FusedAggregateInstruction.StructGetSigned(
                    address = FusedOperand.ValueStack,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                    fieldIndex = instruction.fieldIndex,
                )
                else -> {
                    output.removeLast()
                    FusedAggregateInstruction.StructGetSigned(
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
    is AggregateInstruction.StructGetUnsigned -> {
        var nextIndex = index

        val address = input.getOrNull(index - 1)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (address == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                address == null -> FusedAggregateInstruction.StructGetUnsigned(
                    address = FusedOperand.ValueStack,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                    fieldIndex = instruction.fieldIndex,
                )
                else -> {
                    output.removeLast()
                    FusedAggregateInstruction.StructGetUnsigned(
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
