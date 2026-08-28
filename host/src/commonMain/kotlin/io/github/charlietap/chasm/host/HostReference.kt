package io.github.charlietap.chasm.host

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
typealias HostReference = Long

/**
 * Identifies a Wasm GC type within one Chasm store.
 *
 * Obtain one through [HostGc.runtimeType], [HostGc.structType], or
 * [HostGc.arrayType]. It is not valid with another store.
 */
@JvmInline
value class HostGcType(val id: Int)

/**
 * An opaque handle which keeps a Chasm reference alive until it is released
 * through the [HostReferences] that created it. Use [HostReferences.reference]
 * to retrieve the raw reference.
 *
 * The handle is invalid after release or after its store is destroyed.
 */
@JvmInline
value class HostReferenceRoot(val slot: Int)

/**
 * Describes the storage type and mutability of a struct field or array
 * element. Use the type properties to interpret its raw value.
 */
@JvmInline
value class HostGcFieldInfo(val encoding: Int) {

    val storageKind: Int
        get() = encoding and STORAGE_KIND_MASK

    val mutable: Boolean
        get() = encoding and MUTABLE_MASK != 0

    val isPackedI8: Boolean
        get() = storageKind == PACKED_I8

    val isPackedI16: Boolean
        get() = storageKind == PACKED_I16

    val isI32: Boolean
        get() = storageKind == I32

    val isI64: Boolean
        get() = storageKind == I64

    val isF32: Boolean
        get() = storageKind == F32

    val isF64: Boolean
        get() = storageKind == F64

    val isReference: Boolean
        get() = storageKind == REFERENCE

    val isV128: Boolean
        get() = storageKind == V128

    companion object {
        const val PACKED_I8 = 0
        const val PACKED_I16 = 1
        const val I32 = 2
        const val I64 = 3
        const val F32 = 4
        const val F64 = 5
        const val REFERENCE = 6
        const val V128 = 7

        const val MUTABLE_MASK = 1 shl 3
        const val STORAGE_KIND_MASK = MUTABLE_MASK - 1
    }
}
