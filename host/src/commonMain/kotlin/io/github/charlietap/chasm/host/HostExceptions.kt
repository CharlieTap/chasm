package io.github.charlietap.chasm.host

/**
 * `HostExceptions` is an API for creating, inspecting, and raising WebAssembly
 * exceptions inside host functions.
 *
 * Created and taken exceptions are rooted in the current host reference scope.
 * A pending exception remains rooted by the store until it is taken or raised.
 * Retain an exception's [HostException.rawReference] when it must outlive the
 * current scope.
 *
 * Call [takePending] or [raisePending] only when [hasPending] is true. Payload
 * offsets and lengths must match the exception tag's signature.
 * [HostFunctionException] remains a separate, uncatchable host trap.
 */
interface HostExceptions {

    /** Whether an exception is waiting at the host boundary. */
    val hasPending: Boolean

    /** Creates a scoped exception by copying the payload declared by [tag]. */
    context(resources: HostResources)
    fun create(
        tag: HostTag,
        payload: HostStack,
        payloadOffset: Int,
    ): HostException

    /** Returns the tag carried by [exception]. */
    fun tag(exception: HostException): HostTag

    /** Returns the number of raw payload words carried by [exception]. */
    fun payloadSize(exception: HostException): Int

    /** Reads one raw payload word from [exception]. */
    fun readPayload(
        exception: HostException,
        index: Int,
    ): Long

    /** Copies raw payload words from [exception] into [destination]. */
    fun readPayload(
        exception: HostException,
        sourceOffset: Int,
        destination: LongArray,
        destinationOffset: Int,
        length: Int,
    ): LongArray

    /** Takes the pending exception and roots it in the current scope. */
    fun takePending(): HostException

    /** Raises [exception] into guest code. */
    fun raise(exception: HostException): Nothing

    /** Raises the current pending exception into guest code. */
    fun raisePending(): Nothing
}

/** Creates an exception with this tag from parameters starting at [payload]. */
context(stack: HostStack, resources: HostResources)
inline fun HostTag.createException(payload: HostParameters): HostException =
    resources.exceptions.create(
        tag = this,
        payload = stack,
        payloadOffset = payload,
    )

/** Creates and raises an exception with this tag and payload. */
context(stack: HostStack, resources: HostResources)
inline fun HostTag.raise(payload: HostParameters): Nothing =
    resources.exceptions.raise(createException(payload))

/** Raises this exception into guest code. */
context(resources: HostResources)
inline fun HostException.raise(): Nothing = resources.exceptions.raise(this)
