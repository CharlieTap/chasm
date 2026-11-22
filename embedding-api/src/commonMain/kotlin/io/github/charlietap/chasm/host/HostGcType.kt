package io.github.charlietap.chasm.host

import kotlin.jvm.JvmInline

@JvmInline
value class HostGcType(val id: Int)

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
