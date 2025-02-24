package io.github.charlietap.chasm.type

data class FieldType(
    var storageType: StorageType,
    var mutability: Mutability,
) : Type
