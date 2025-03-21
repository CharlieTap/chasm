package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType

internal fun StorageTypeSubstitutor(
    storageType: StorageType,
    substitution: Substitution,
): StorageType =
    StorageTypeSubstitutor(
        storageType = storageType,
        substitution = substitution,
        valueTypeSubstitutor = ::ValueTypeSubstitutor,
    )

internal fun StorageTypeSubstitutor(
    storageType: StorageType,
    substitution: Substitution,
    valueTypeSubstitutor: TypeSubstitutor<ValueType>,
): StorageType = when (storageType) {
    is StorageType.Packed -> storageType
    is StorageType.Value -> {
        StorageType.Value(valueTypeSubstitutor(storageType.type, substitution))
    }
}
