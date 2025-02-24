package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.StorageType

fun fieldType() = immutableFieldType()

fun mutableFieldType(
    storageType: StorageType = storageType(),
    mutability: Mutability = mutability(),
) = FieldType(storageType, mutability)

fun immutableFieldType(
    storageType: StorageType = storageType(),
    mutability: Mutability = mutability(),
) = FieldType(storageType, mutability)
