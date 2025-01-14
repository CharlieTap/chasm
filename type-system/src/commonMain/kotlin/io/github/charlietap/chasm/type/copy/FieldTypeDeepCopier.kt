package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StorageType

fun FieldTypeDeepCopier(
    input: FieldType,
): FieldType =
    FieldTypeDeepCopier(
        input = input,
        storageTypeCopier = ::StorageTypeDeepCopier,
    )

inline fun FieldTypeDeepCopier(
    input: FieldType,
    storageTypeCopier: DeepCopier<StorageType>,
): FieldType = input.copy(
    storageType = storageTypeCopier(input.storageType),
    mutability = input.mutability,
)
