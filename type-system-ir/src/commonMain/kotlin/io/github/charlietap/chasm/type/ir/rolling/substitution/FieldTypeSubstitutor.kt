package io.github.charlietap.chasm.type.ir.rolling.substitution

import io.github.charlietap.chasm.ir.type.FieldType
import io.github.charlietap.chasm.ir.type.StorageType

internal fun FieldTypeSubstitutor(
    fieldType: FieldType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): FieldType =
    FieldTypeSubstitutor(
        fieldType = fieldType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        storageTypeSubstitutor = ::StorageTypeSubstitutor,
    )

internal fun FieldTypeSubstitutor(
    fieldType: FieldType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    storageTypeSubstitutor: TypeSubstitutor<StorageType>,
): FieldType = fieldType.apply {
    storageType = storageTypeSubstitutor(fieldType.storageType, concreteHeapTypeSubstitutor)
}
