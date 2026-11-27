package io.github.charlietap.chasm.runtime.host

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.jvm.JvmInline

/**
 * Chasm's raw, unboxed WebAssembly reference representation.
 *
 * ```
 * 63                     8 7                      0
 * +-----------------------+------------------------+
 * |        payload        |      type tag (8)      |
 * +-----------------------+------------------------+
 * ```
 *
 * The type tag is `reference and 0xff`; the payload is `reference shr 8`.
 *
 * - `1`: null; payload is the heap type
 * - `2`: i31; payload is the 31-bit value
 * - `3`: struct; payload is the heap address
 * - `4`: array; payload is the heap address
 * - `5`: function; payload is the store address
 * - `6`: host value; payload is the host-value slot
 * - `7`: exception; payload is the heap address
 * - `8`: extern; payload is another encoded reference
 *
 * Address payloads are only valid in the store that produced them.
 */
@InternalChasmApi
typealias HostReference = Long

/**
 * An opaque handle which keeps a Chasm reference alive until it is released
 * through the [HostReferences] that created it. Use [HostReferences.reference]
 * to retrieve the raw reference.
 *
 * The handle is invalid after release or after its store is destroyed.
 */
@InternalChasmApi
@JvmInline
value class HostReferenceRoot(val slot: Int)

/**
 * An `externref` encoded as a raw Chasm reference.
 *
 * Use [raw] when passing it through a host stack, global, or table. The value
 * belongs to the store which created it.
 */
@InternalChasmApi
@JvmInline
value class HostExternReference(val raw: HostReference)

/** Describes what a [HostExternReference] contains. */
@InternalChasmApi
@JvmInline
value class HostExternKind(val id: Int) {

    companion object {
        val NULL = HostExternKind(0)
        val HOST_VALUE = HostExternKind(1)
        val EXTERNALIZED_REFERENCE = HostExternKind(2)
    }
}
