package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedDestination
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
    destinationFactory = ::FusedDestinationFactory,
)

internal inline fun ReferenceInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: ReferenceInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    destinationFactory: FusedDestinationFactory,
): Int = when (instruction) {
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
    else -> {
        output.add(instruction)
        index
    }
}
