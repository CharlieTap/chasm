package io.github.charlietap.chasm.config

/**
 * Configures a store.
 *
 * @property meterFuel compiles a fuel check at every function entry and loop iteration of the
 * store's code. A metered store starts with no fuel, so add some before running any of its code,
 * a module's start function included.
 * @property interruptible compiles an interrupt check at every function entry and loop iteration
 * of the store's code, so another thread can stop a running call. A store with both settings
 * makes one combined check at each site.
 * @property gcStrategy decides when the store's GC heap is collected. When `null`, each instance
 * uses the strategy from the [RuntimeConfig] it was instantiated with.
 * @property gcThreshold sets how much the store's GC heap may hold before an automatic
 * collection. When `null`, each instance uses the threshold from the [RuntimeConfig] it was
 * instantiated with.
 * @property limits bounds the memory that code running in the store can consume.
 */
data class StoreConfig(
    val meterFuel: Boolean = false,
    val interruptible: Boolean = false,
    val gcStrategy: GCStrategy? = null,
    val gcThreshold: GCThreshold? = null,
    val limits: StoreLimits = StoreLimits(),
) {
    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    constructor(
        meterFuel: Boolean = false,
        interruptible: Boolean = false,
    ) : this(meterFuel, interruptible, null, null, StoreLimits())

    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    fun copy(
        meterFuel: Boolean = this.meterFuel,
        interruptible: Boolean = this.interruptible,
    ): StoreConfig = copy(meterFuel, interruptible, gcStrategy, gcThreshold, limits)
}
