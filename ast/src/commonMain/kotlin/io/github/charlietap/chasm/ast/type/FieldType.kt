package io.github.charlietap.chasm.ast.type

data class FieldType(
    var storageType: StorageType,
    var mutability: Mutability,
) : Type
