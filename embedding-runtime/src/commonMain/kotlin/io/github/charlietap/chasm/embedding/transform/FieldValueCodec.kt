package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.toExecutionValue
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.FieldValue
import io.github.charlietap.chasm.runtime.value.PackedValue
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType

internal typealias FieldValueDecoder = (Long, FieldType) -> FieldValue

internal typealias FieldValueEncoder = (FieldValue, FieldType) -> Long

internal fun FieldValueDecoder(
    value: Long,
    fieldType: FieldType,
): FieldValue = when (val storageType = fieldType.storageType) {
    is StorageType.Value -> when (storageType.type) {
        is ValueType.Number,
        is ValueType.Reference,
        -> FieldValue.Execution(value.toExecutionValue(storageType.type))
        is ValueType.Bottom,
        is ValueType.Vector,
        -> throw InvocationException(InvocationError.UnobservableBitWidth)
    }
    is StorageType.Packed -> when (storageType.type) {
        PackedType.I8 -> FieldValue.Packed(PackedValue.I8(value))
        PackedType.I16 -> FieldValue.Packed(PackedValue.I16(value))
    }
}

internal fun FieldValueEncoder(
    value: FieldValue,
    fieldType: FieldType,
): Long = when (val storageType = fieldType.storageType) {
    is StorageType.Value -> when (storageType.type) {
        is ValueType.Number,
        is ValueType.Reference,
        -> (value as? FieldValue.Execution)?.executionValue?.toLongFromBoxed()
            ?: throw InvocationException(valueExpected(storageType.type))
        is ValueType.Bottom,
        is ValueType.Vector,
        -> throw InvocationException(InvocationError.UnobservableBitWidth)
    }
    is StorageType.Packed -> when (storageType.type) {
        PackedType.I8 -> ((value as? FieldValue.Packed)?.packedValue as? PackedValue.I8)?.value?.and(0xFFL)
            ?: throw InvocationException(InvocationError.PackedValueExpected)
        PackedType.I16 -> ((value as? FieldValue.Packed)?.packedValue as? PackedValue.I16)?.value?.and(0xFFFFL)
            ?: throw InvocationException(InvocationError.PackedValueExpected)
    }
}

private fun valueExpected(type: ValueType): InvocationError = when (type) {
    is ValueType.Number -> InvocationError.NumberValueExpected
    is ValueType.Reference -> InvocationError.ReferenceValueExpected
    is ValueType.Bottom,
    is ValueType.Vector,
    -> InvocationError.UnobservableBitWidth
}
