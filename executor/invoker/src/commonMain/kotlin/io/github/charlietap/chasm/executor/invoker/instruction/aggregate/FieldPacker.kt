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

internal typealias FieldPacker = (ExecutionValue, FieldType) -> FieldValue

internal fun FieldPacker(
    value: ExecutionValue,
    fieldType: FieldType,
): FieldValue {
    return when (val storageType = fieldType.storageType) {
        is StorageType.Packed -> {

            val numberValue = try {
                value as NumberValue.I32
            } catch (_: ClassCastException) {
                throw InvocationException(InvocationError.NumberValueExpected)
            }

            val packedValue = when (storageType.type) {
                PackedType.I8 -> {
                    PackedValue.I8(numberValue.value.toUByte())
                }

                PackedType.I16 -> {
                    PackedValue.I16(numberValue.value.toUShort())
                }
            }

            FieldValue.Packed(packedValue)
        }

        is StorageType.Value -> {
            FieldValue.Execution(value)
        }
    }
}
