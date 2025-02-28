package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.FusedReferenceInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction

internal typealias ReferenceInstructionFuser = (PassContext, Int, ReferenceInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun ReferenceInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: ReferenceInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = ReferenceInstructionFuser(
    context = context,
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    operandFactory = ::FusedOperandFactory,
    destinationFactory = ::FusedDestinationFactory,
)

internal inline fun ReferenceInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: ReferenceInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    operandFactory: FusedOperandFactory,
    destinationFactory: FusedDestinationFactory,
): Int = when (instruction) {
    is ReferenceInstruction.RefEq -> {
        var nextIndex = index

        val reference1 = input.getOrNull(index - 1)?.let(operandFactory)
        val reference2 = input.getOrNull(index - 2)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (reference1 == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                reference1 == null -> FusedReferenceInstruction.RefEq(
                    reference1 = FusedOperand.ValueStack,
                    reference2 = FusedOperand.ValueStack,
                    destination = destination,
                )
                reference2 == null -> {
                    output.removeLast()
                    FusedReferenceInstruction.RefEq(
                        reference1 = reference1,
                        reference2 = FusedOperand.ValueStack,
                        destination = destination,
                    )
                }
                else -> {
                    output.removeLast()
                    output.removeLast()
                    FusedReferenceInstruction.RefEq(
                        reference1 = reference1,
                        reference2 = reference2,
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
    is ReferenceInstruction.RefIsNull -> {
        var nextIndex = index

        val value = input.getOrNull(index - 1)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (value == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                value == null -> FusedReferenceInstruction.RefIsNull(
                    value = FusedOperand.ValueStack,
                    destination = destination,
                )
                else -> {
                    output.removeLast()
                    FusedReferenceInstruction.RefIsNull(
                        value = value,
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
    is ReferenceInstruction.RefNull -> {
        var nextIndex = index

        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (destination == FusedDestination.ValueStack) {
            instruction
        } else {
            FusedReferenceInstruction.RefNull(
                destination = destination,
                type = instruction.type,
            )
        }

        output.add(instruction)

        if (destination != FusedDestination.ValueStack) {
            nextIndex++
        }

        nextIndex
    }
    is ReferenceInstruction.RefTest -> {
        var nextIndex = index

        val reference = input.getOrNull(index - 1)?.let(operandFactory)
        val destination = input.getOrNull(index + 1).let(destinationFactory)

        val instruction = if (reference == null && destination == FusedDestination.ValueStack) {
            instruction
        } else {
            when {
                reference == null -> FusedReferenceInstruction.RefTest(
                    reference = FusedOperand.ValueStack,
                    destination = destination,
                    referenceType = instruction.referenceType,
                )
                else -> {
                    output.removeLast()
                    FusedReferenceInstruction.RefTest(
                        reference = reference,
                        destination = destination,
                        referenceType = instruction.referenceType,
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
