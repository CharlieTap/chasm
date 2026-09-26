package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Store

/**
 * Stops the call running on a store created with `StoreConfig(interruptible = true)`. Safe to call
 * from any thread: the call traps with `Interrupted` at its next function entry or loop iteration.
 * A host function the call is running is only stopped once it returns to Wasm code. An interrupt
 * made while no call runs has no effect.
 */
fun interrupt(store: Store): ChasmResult<Unit, ChasmError.ExecutionError> {
    val interrupt = store.store.interrupt
    if (!interrupt.enabled) {
        return ChasmResult.Error(ChasmError.ExecutionError("Interrupts are not enabled for this store"))
    }

    interrupt.requested = true
    return ChasmResult.Success(Unit)
}
