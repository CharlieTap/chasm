package io.github.charlietap.chasm.type.ir.rolling.substitution

import io.github.charlietap.chasm.ir.type.StorageType
import io.github.charlietap.chasm.ir.type.ValueType

internal fun StorageTypeSubstitutor(
    storageType: StorageType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): StorageType =
    StorageTypeSubstitutor(
        storageType = storageType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        valueTypeSubstitutor = ::ValueTypeSubstitutor,
    )

internal fun StorageTypeSubstitutor(
    storageType: StorageType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    valueTypeSubstitutor: TypeSubstitutor<ValueType>,
): StorageType = when (storageType) {
    is StorageType.Packed -> storageType
    is StorageType.Value -> {
        StorageType.Value(valueTypeSubstitutor(storageType.type, concreteHeapTypeSubstitutor))
    }
}
