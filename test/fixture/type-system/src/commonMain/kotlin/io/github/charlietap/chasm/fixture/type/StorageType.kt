package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType

fun storageType() = packedStorageType()

fun packedStorageType(
    packedType: PackedType = packedType(),
) = StorageType.Packed(packedType)

fun valueStorageType(
    valueType: ValueType = valueType(),
) = StorageType.Value(valueType)
