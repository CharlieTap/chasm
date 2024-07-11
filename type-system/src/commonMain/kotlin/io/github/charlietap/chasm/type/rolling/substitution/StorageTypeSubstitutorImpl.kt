package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.ast.type.ValueType

internal fun StorageTypeSubstitutorImpl(
    storageType: StorageType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): StorageType =
    StorageTypeSubstitutorImpl(
        storageType = storageType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        valueTypeSubstitutor = ::ValueTypeSubstitutorImpl,
    )

internal fun StorageTypeSubstitutorImpl(
    storageType: StorageType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    valueTypeSubstitutor: TypeSubstitutor<ValueType>,
): StorageType = when (storageType) {
    is StorageType.Packed -> storageType
    is StorageType.Value -> {
        StorageType.Value(valueTypeSubstitutor(storageType.type, concreteHeapTypeSubstitutor))
    }
}
