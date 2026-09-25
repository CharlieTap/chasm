package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Store

/**
 * Adds [amount] units to a store created with `StoreConfig(meterFuel = true)`, saturating at
 * [Long.MAX_VALUE]. Its code takes one at every function entry and loop iteration, and traps with
 * `FuelExhausted` once none are left. Instantiating a module runs its start function on the same fuel.
 * Call it only while none of the store's calls are running.
 */
fun addFuel(
    store: Store,
    amount: Long,
): ChasmResult<Unit, ChasmError.ExecutionError> {
    val fuel = store.store.fuel
    if (!fuel.metered) return unmeteredError()
    if (amount < 0L) {
        return ChasmResult.Error(ChasmError.ExecutionError("Fuel added must be non-negative"))
    }

    fuel.remaining += amount.coerceAtMost(Long.MAX_VALUE - fuel.remaining)
    return ChasmResult.Success(Unit)
}

/** Empties a metered store's fuel; call it only while none of its calls run. */
fun resetFuel(store: Store): ChasmResult<Unit, ChasmError.ExecutionError> {
    val fuel = store.store.fuel
    if (!fuel.metered) return unmeteredError()

    fuel.remaining = 0L
    return ChasmResult.Success(Unit)
}

/** The fuel a metered store has left; call it only while none of its calls run. */
fun remainingFuel(store: Store): ChasmResult<Long, ChasmError.ExecutionError> {
    val fuel = store.store.fuel
    if (!fuel.metered) return unmeteredError()

    return ChasmResult.Success(fuel.remaining)
}

private fun unmeteredError() = ChasmResult.Error(
    ChasmError.ExecutionError("Fuel metering is not enabled for this store"),
)
