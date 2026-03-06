package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun CopySlotsInstructionExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.CopySlots,
) {
    val copiedValues = LongArray(instruction.sourceSlots.size) { index ->
        vstack.getFrameSlot(instruction.sourceSlots[index])
    }

    instruction.destinationSlots.forEachIndexed { index, slot ->
        vstack.setFrameSlot(slot, copiedValues[index])
    }
}
