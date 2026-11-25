package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.embedding.value.VectorValue
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType

internal fun argumentMismatch(
    type: FunctionType,
    args: List<ExecutionValue>,
): String? {

    val params = type.params.types
    if (args.size != params.size) {
        return "ArgumentCountMismatch(expected=${params.size}, actual=${args.size})"
    }

    val index = params.indices.firstOrNull { index -> !matchesType(params[index], args[index]) } ?: return null

    return "ArgumentTypeMismatch(index=$index, expected=${params[index]}, actual=${args[index]})"
}

internal fun matchesType(
    type: ValueType,
    value: ExecutionValue,
): Boolean {
    return when (type) {
        is ValueType.Number -> when (type.numberType) {
            NumberType.I32 -> value is NumberValue.I32
            NumberType.I64 -> value is NumberValue.I64
            NumberType.F32 -> value is NumberValue.F32
            NumberType.F64 -> value is NumberValue.F64
        }
        is ValueType.Vector -> value is VectorValue.V128
        is ValueType.Reference -> {
            value is ReferenceValue && (type.referenceType is ReferenceType.RefNull || value !is ReferenceValue.Null)
        }
        is ValueType.Bottom -> false
    }
}

internal fun usesVector(type: FunctionType): Boolean {
    return type.params.types.any { valueType -> valueType is ValueType.Vector } ||
        type.results.types.any { valueType -> valueType is ValueType.Vector }
}

internal fun referenceType(type: ValueType): ReferenceType? {
    return (type as? ValueType.Reference)?.referenceType
}

internal fun referenceType(field: FieldType): ReferenceType? {

    val storage = field.storageType as? StorageType.Value ?: return null

    return referenceType(storage.type)
}

internal fun packedValue(
    field: FieldType,
    value: Long,
): Long {
    return when ((field.storageType as? StorageType.Packed)?.type) {
        PackedType.I8 -> value and BYTE_MASK
        PackedType.I16 -> value and SHORT_MASK
        null -> value
    }
}

private const val BYTE_MASK = 0xFFL
private const val SHORT_MASK = 0xFFFFL
