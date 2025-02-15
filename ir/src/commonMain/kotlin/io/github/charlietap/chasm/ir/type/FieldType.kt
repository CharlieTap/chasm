package io.github.charlietap.chasm.ir.type

data class FieldType(
    var storageType: StorageType,
    var mutability: Mutability,
) : Type
