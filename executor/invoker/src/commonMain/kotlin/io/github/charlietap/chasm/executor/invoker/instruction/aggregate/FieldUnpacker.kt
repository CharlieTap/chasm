package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.FieldValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.PackedValue

internal typealias FieldUnpacker = (FieldValue.Packed, Boolean) -> ExecutionValue

internal fun FieldUnpacker(
    value: FieldValue.Packed,
    signedUnpack: Boolean,
): ExecutionValue {
    val unpacked = when (val packed = value.packedValue) {
        is PackedValue.I8 -> {
            if (signedUnpack) {
                packed.value.toByte().toInt()
            } else {
                packed.value.toInt() and 0xFF
            }
        }
        is PackedValue.I16 -> {
            if (signedUnpack) {
                packed.value.toShort().toInt()
            } else {
                packed.value.toInt() and 0xFFFF
            }
        }
    }

    return NumberValue.I32(unpacked)
}
