package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.store.Fuel

/** [fuel] is the compiling store's, captured so a check does not look it up on every loop iteration. */
fun FuelCheckDispatcher(
    @Suppress("UNUSED_PARAMETER") instruction: AdminInstruction.FuelCheck,
    fuel: Fuel,
): DispatchableInstruction = DispatchableInstruction { _, _, nextIp ->
    if (fuel.remaining <= 0L) {
        throw InvocationException(InvocationError.FuelExhausted)
    }
    fuel.remaining--
    nextIp
}
