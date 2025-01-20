package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.FieldValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.PackedValue

internal typealias FieldUnpacker = (FieldValue, FieldType, Boolean) -> ExecutionValue

internal fun FieldUnpacker(
    value: FieldValue,
    fieldType: FieldType,
    signedUnpack: Boolean,
): ExecutionValue {
    return try {
        when (val storageType = fieldType.storageType) {
            is StorageType.Packed -> {

                val packedValue = value as FieldValue.Packed

                val packedInt = when (storageType.type) {
                    PackedType.I8 -> {
                        val i8 = packedValue.packedValue as PackedValue.I8
                        if (signedUnpack) {
                            i8.value.toByte().toInt()
                        } else {
                            i8.value.toInt() and 0xFF
                        }
                    }
                    PackedType.I16 -> {
                        val i16 = packedValue.packedValue as PackedValue.I16
                        if (signedUnpack) {
                            i16.value.toShort().toInt()
                        } else {
                            i16.value.toInt() and 0xFFFF
                        }
                    }
                }

                NumberValue.I32(packedInt)
            }

            is StorageType.Value -> {
                (value as FieldValue.Execution).executionValue
            }
        }
    } catch (_: ClassCastException) {
        throw InvocationException(InvocationError.PackedValueExpected)
    }
}
