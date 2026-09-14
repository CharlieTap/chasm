package io.github.charlietap.chasm.executor.invoker.instruction

import io.github.charlietap.chasm.runtime.instruction.GcAllocationInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun ensureFrameRoots(vstack: ValueStack, instruction: GcAllocationInstruction) {
    // Calls and catches restore an operand prefix. Later slot writes do not
    // grow sp, so allocation preflight restores the complete frame root range.
    vstack.reserveDepth(vstack.fp + instruction.frameSlots)
}
