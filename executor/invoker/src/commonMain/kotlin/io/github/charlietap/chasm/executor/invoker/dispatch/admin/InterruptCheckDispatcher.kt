package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.store.Interrupt

/** [interrupt] is the compiling store's, captured so a check does not look it up on every loop iteration. */
fun InterruptCheckDispatcher(
    @Suppress("UNUSED_PARAMETER") instruction: AdminInstruction.InterruptCheck,
    interrupt: Interrupt,
): DispatchableInstruction = DispatchableInstruction { _, _, nextIp ->
    if (interrupt.requested) {
        throw InvocationException(InvocationError.Interrupted)
    }
    nextIp
}
