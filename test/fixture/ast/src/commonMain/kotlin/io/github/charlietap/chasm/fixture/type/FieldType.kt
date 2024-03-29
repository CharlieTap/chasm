package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StorageType

fun fieldType() = immutableFieldType()

fun mutableFieldType(
    storageType: StorageType = storageType(),
) = FieldType.MutableStorageType(storageType)

fun immutableFieldType(
    storageType: StorageType = storageType(),
) = FieldType.ImmutableStorageType(storageType)
