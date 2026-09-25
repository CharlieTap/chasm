package io.github.charlietap.chasm.runtime.store

/**
 * A budget for the code a store runs. When [metered], code compiled into the store takes a unit from
 * [remaining] at every function entry and loop iteration: [remaining] of them pass, and the next traps
 * with [io.github.charlietap.chasm.runtime.error.InvocationError.FuelExhausted]. Unmetered stores
 * compile no checks and pay nothing.
 */
class Fuel(val metered: Boolean = false) {
    /** Read and written only by the thread running the store's calls. */
    var remaining: Long = 0L
}
