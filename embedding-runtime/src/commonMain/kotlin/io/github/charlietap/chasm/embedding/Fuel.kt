package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.store.Fuel

/**
 * Adds [amount] units to a store created with `StoreConfig(meterFuel = true)`, saturating at
 * [Long.MAX_VALUE]. Its code takes one at every function entry and loop iteration, and traps with
 * `FuelExhausted` once none are left. Instantiating a module runs its start function on the same fuel.
 * Call it only while none of the store's calls are running.
 */
fun addFuel(
    store: Store,
    amount: Long,
) {

    val fuel = meteredFuel(store)
    require(amount >= 0L) { "Fuel added must be non-negative" }

    fuel.remaining += amount.coerceAtMost(Long.MAX_VALUE - fuel.remaining)
}

/** Empties a metered store's fuel; call it only while none of its calls run. */
fun resetFuel(store: Store) {
    meteredFuel(store).remaining = 0L
}

/** The fuel a metered store has left; call it only while none of its calls run. */
fun remainingFuel(store: Store): Long {
    return meteredFuel(store).remaining
}

private fun meteredFuel(store: Store): Fuel {

    check(!store.isDropped) { InvocationError.StoreDropped.toString() }

    val fuel = store.store.fuel
    check(fuel.metered) { "Fuel metering is not enabled for this store" }

    return fuel
}
