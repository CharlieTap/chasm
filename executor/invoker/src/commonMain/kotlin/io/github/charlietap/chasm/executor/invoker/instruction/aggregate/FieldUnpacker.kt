package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.type.PackedType

internal typealias FieldUnpacker = (Long, PackedType, Boolean) -> Long

internal fun FieldUnpacker(
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
