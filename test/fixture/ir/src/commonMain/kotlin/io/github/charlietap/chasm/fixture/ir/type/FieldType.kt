package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.FieldType
import io.github.charlietap.chasm.ir.type.Mutability
import io.github.charlietap.chasm.ir.type.StorageType

fun fieldType() = immutableFieldType()

fun mutableFieldType(
    storageType: StorageType = storageType(),
    mutability: Mutability = mutability(),
) = FieldType(storageType, mutability)

fun immutableFieldType(
    storageType: StorageType = storageType(),
    mutability: Mutability = mutability(),
) = FieldType(storageType, mutability)
