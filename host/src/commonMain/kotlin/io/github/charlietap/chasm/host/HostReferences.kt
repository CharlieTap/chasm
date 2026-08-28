package io.github.charlietap.chasm.host

/**
 * `HostReferences` is an API that lets host code keep Chasm references alive
 * by registering roots with Chasm's garbage collector, then ending or
 * releasing them when they are no longer needed.
 *
 * Chasm traces references in the active Wasm stack, globals, tables, and live
 * guest objects. It cannot see a raw [HostReference] held only in Kotlin. A
 * scoped root keeps that reference alive until its scope ends. A retained root
 * keeps it alive until [release] is called.
 *
 * Every host callback has a scope. Host APIs which create references add them
 * to it automatically. Use [withScope] for a shorter nested lifetime, or
 * [retain] when a reference must survive beyond the callback.
 *
 * Scopes must end in LIFO order. Markers and roots belong to this instance and
 * its store. Roots are invalid after release, and all markers and roots are
 * invalid after the store is destroyed.
 */
interface HostReferences {

    /**
     * Starts a nested scope and returns the marker needed to end it.
     * [capacity] reserves room for roots; it does not limit the scope.
     */
    fun beginScope(capacity: Int = 0): Int

    /** Keeps [reference] alive until the current scope ends and returns it unchanged. */
    fun rootScoped(reference: HostReference): HostReference

    /** Ends the current scope and stops tracing roots added after [marker]. */
    fun endScope(marker: Int)

    /** Keeps [reference] alive until [release] is called. Each call creates a separate root. */
    fun retain(reference: HostReference): HostReferenceRoot

    /** Returns the raw reference held by [root]. */
    fun reference(root: HostReferenceRoot): HostReference

    /**
     * Stops keeping [root] alive. Its reference may then be collected if
     * nothing else traced by Chasm can reach it.
     */
    fun release(root: HostReferenceRoot)
}

/** Runs [block] in a nested reference scope and always ends that scope. */
inline fun <T> HostReferences.withScope(
    capacity: Int = 0,
    block: HostReferences.() -> T,
): T {
    val marker = beginScope(capacity)
    try {
        return block()
    } finally {
        endScope(marker)
    }
}
