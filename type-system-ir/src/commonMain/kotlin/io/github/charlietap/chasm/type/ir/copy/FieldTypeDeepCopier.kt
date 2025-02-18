package io.github.charlietap.chasm.type.ir.copy

import io.github.charlietap.chasm.ir.type.FieldType
import io.github.charlietap.chasm.ir.type.StorageType

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
