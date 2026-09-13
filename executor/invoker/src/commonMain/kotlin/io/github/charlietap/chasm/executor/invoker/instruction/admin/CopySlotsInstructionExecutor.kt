package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun CopySlotInstructionExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: AdminInstruction.CopySlot,
) {
    vstack.setFrameSlot(instruction.destinationSlot, vstack.getFrameSlot(instruction.sourceSlot))
}

internal inline fun CopySlotsInstructionExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: AdminInstruction.CopySlots,
) {
    if (instruction.sequential) {
        for (index in instruction.sourceSlots.indices) {
            vstack.setFrameSlot(instruction.destinationSlots[index], vstack.getFrameSlot(instruction.sourceSlots[index]))
        }
        return
    }
    val copiedValues = LongArray(instruction.sourceSlots.size) { index ->
        vstack.getFrameSlot(instruction.sourceSlots[index])
    }

    for (index in instruction.destinationSlots.indices) {
        vstack.setFrameSlot(instruction.destinationSlots[index], copiedValues[index])
    }
}
