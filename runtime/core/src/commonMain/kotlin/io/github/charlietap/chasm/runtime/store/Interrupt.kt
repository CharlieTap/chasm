package io.github.charlietap.chasm.runtime.store

import kotlin.concurrent.Volatile

/**
 * A request to stop the code a store runs. When [enabled], code compiled into the store checks
 * [requested] at every function entry and loop iteration, and traps with
 * [io.github.charlietap.chasm.runtime.error.InvocationError.Interrupted] once it is set. Stores
 * without it compile no checks and pay nothing.
 */
class Interrupt(val enabled: Boolean = false) {
    /**
     * May be set from any thread. Stays set until the next outermost call starts, so a call made
     * back into the store from a host function cannot swallow an interrupt meant for its caller.
     */
    @Volatile
    var requested: Boolean = false

    /** Calls running on the store, a host function's calls back into it included. */
    var depth: Int = 0
}
