package io.github.charlietap.chasm.config

/**
 * Configures a store.
 *
 * @property meterFuel compiles a fuel check at every function entry and loop iteration of the
 * store's code. A metered store starts with no fuel, so add some before running any of its code,
 * a module's start function included.
 */
data class StoreConfig(
    val meterFuel: Boolean = false,
)
