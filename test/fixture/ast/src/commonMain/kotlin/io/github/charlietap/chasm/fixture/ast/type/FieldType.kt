package io.github.charlietap.chasm.fixture.ast.type

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.ast.type.StorageType

fun fieldType() = immutableFieldType()

fun mutableFieldType(
    storageType: StorageType = storageType(),
    mutability: Mutability = mutability(),
) = FieldType(storageType, mutability)

fun immutableFieldType(
    storageType: StorageType = storageType(),
    mutability: Mutability = mutability(),
) = FieldType(storageType, mutability)
