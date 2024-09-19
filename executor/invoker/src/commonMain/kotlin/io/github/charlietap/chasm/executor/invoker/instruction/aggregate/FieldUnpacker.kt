package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.FieldValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.PackedValue

internal typealias FieldUnpacker = (FieldValue, FieldType, Boolean) -> Result<ExecutionValue, InvocationError>

internal fun FieldUnpacker(
    value: FieldValue,
    fieldType: FieldType,
    signedUnpack: Boolean,
): Result<ExecutionValue, InvocationError> {
    return when (val storageType = fieldType.storageType) {
        is StorageType.Packed -> {

            val packedValue = value as? FieldValue.Packed
                ?: return Err(InvocationError.PackedValueExpected)

            val packedInt = when (storageType.type) {
                PackedType.I8 -> {
                    (packedValue.packedValue as? PackedValue.I8)?.value?.let { byteValue ->
                        if (signedUnpack) {
                            byteValue.toByte().toInt()
                        } else {
                            byteValue.toInt() and 0xFF
                        }
                    }
                }

                PackedType.I16 -> {
                    (packedValue.packedValue as? PackedValue.I16)?.value?.let { shortValue ->
                        if (signedUnpack) {
                            shortValue.toShort().toInt()
                        } else {
                            shortValue.toInt() and 0xFFFF
                        }
                    }
                }
            } ?: return Err(InvocationError.PackedValueExpected)

            NumberValue.I32(packedInt)
        }

        is StorageType.Value -> {
            (value as FieldValue.Execution).executionValue
        }
    }.let(::Ok)
}
