package io.github.charlietap.chasm.runtime.operations

import io.github.charlietap.chasm.type.PackedType

typealias FieldUnpacker = (Long, PackedType, Boolean) -> Long

fun FieldUnpacker(
    value: Long,
    type: PackedType,
    signedUnpack: Boolean,
): Long {
    val unpacked = when (type) {
        is PackedType.I8 -> if (signedUnpack) {
            value.toByte().toInt()
        } else {
            value.toInt() and 0xFF
        }
        is PackedType.I16 -> if (signedUnpack) {
            value.toShort().toInt()
        } else {
            value.toInt() and 0xFFFF
        }
    }

    return unpacked.toLong()
}
