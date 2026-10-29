package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Store

/**
 * Stops the call running on a store created with `StoreConfig(interruptible = true)`. Safe to call
 * from any thread: Wasm code in the call traps with `Interrupted` at its next function entry or loop
 * iteration. A host function the call is running is not stopped itself; Wasm code it calls back into
 * traps, as does its Wasm caller once it returns.
 *
 * Returns `true` if a call was running to receive the interrupt, and `false` if none was, in which
 * case the interrupt is dropped and the next call runs normally.
 */
fun interrupt(store: Store): ChasmResult<Boolean, ChasmError.ExecutionError> {
    val interrupt = store.store.interrupt
    if (!interrupt.enabled) {
        return ChasmResult.Error(ChasmError.ExecutionError("Interrupts are not enabled for this store"))
    }
    if (interrupt.depth == 0) return ChasmResult.Success(false)

    interrupt.requested = true
    return ChasmResult.Success(true)
}
