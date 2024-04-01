package io.github.charlietap.chasm.ast.type

data class FieldType(
    val storageType: StorageType,
    val mutability: Mutability,
) : Type
