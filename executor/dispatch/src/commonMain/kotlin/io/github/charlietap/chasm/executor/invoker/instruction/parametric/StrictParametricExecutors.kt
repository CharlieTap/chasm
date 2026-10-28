package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeSelect(
    vstack: ValueStack,
    condition: Long,
    val1: Long,
    val2: Long,
    destinationSlot: Int,
) {
    if (condition == 0L) {
        vstack.setFrameSlot(destinationSlot, val2)
    } else {
        vstack.setFrameSlot(destinationSlot, val1)
    }
}
