package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.store.Fuel
import io.github.charlietap.chasm.runtime.store.Interrupt

/** [fuel] and [interrupt] are the compiling store's, captured so a check does not look them up on every loop iteration. */
fun FuelAndInterruptCheckDispatcher(
    @Suppress("UNUSED_PARAMETER") instruction: AdminInstruction.FuelAndInterruptCheck,
    fuel: Fuel,
    interrupt: Interrupt,
): DispatchableInstruction = DispatchableInstruction { _, _, nextIp ->
    if (fuel.remaining <= 0L) {
        throw InvocationException(InvocationError.FuelExhausted)
    }
    if (interrupt.requested) {
        throw InvocationException(InvocationError.Interrupted)
    }
    fuel.remaining--
    nextIp
}
