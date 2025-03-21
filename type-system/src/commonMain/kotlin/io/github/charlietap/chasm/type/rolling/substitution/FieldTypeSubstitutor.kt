package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StorageType

internal fun FieldTypeSubstitutor(
    fieldType: FieldType,
    substitution: Substitution,
): FieldType =
    FieldTypeSubstitutor(
        fieldType = fieldType,
        substitution = substitution,
        storageTypeSubstitutor = ::StorageTypeSubstitutor,
    )

internal fun FieldTypeSubstitutor(
    fieldType: FieldType,
    substitution: Substitution,
    storageTypeSubstitutor: TypeSubstitutor<StorageType>,
): FieldType = fieldType.apply {
    storageType = storageTypeSubstitutor(fieldType.storageType, substitution)
}
