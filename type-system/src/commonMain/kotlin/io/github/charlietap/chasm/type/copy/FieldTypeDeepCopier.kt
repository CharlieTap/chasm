package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StorageType

internal fun FieldTypeDeepCopier(
    input: FieldType,
): FieldType =
    FieldTypeDeepCopier(
        input = input,
        storageTypeCopier = ::StorageTypeDeepCopier,
    )

internal inline fun FieldTypeDeepCopier(
    input: FieldType,
    storageTypeCopier: DeepCopier<StorageType>,
): FieldType = input.copy(
    storageType = storageTypeCopier(input.storageType),
    mutability = input.mutability,
)
