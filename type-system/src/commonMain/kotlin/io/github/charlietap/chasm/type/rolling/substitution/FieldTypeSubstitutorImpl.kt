package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StorageType

internal fun FieldTypeSubstitutorImpl(
    fieldType: FieldType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): FieldType =
    FieldTypeSubstitutorImpl(
        fieldType = fieldType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        storageTypeSubstitutor = ::StorageTypeSubstitutorImpl,
    )

internal fun FieldTypeSubstitutorImpl(
    fieldType: FieldType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    storageTypeSubstitutor: TypeSubstitutor<StorageType>,
): FieldType = fieldType.copy(
    storageTypeSubstitutor(fieldType.storageType, concreteHeapTypeSubstitutor),
)
