package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.PackedType
import io.github.charlietap.chasm.ir.type.StorageType
import io.github.charlietap.chasm.ir.type.ValueType

fun storageType() = packedStorageType()

fun packedStorageType(
    packedType: PackedType = packedType(),
) = StorageType.Packed(packedType)

fun valueStorageType(
    valueType: ValueType = valueType(),
) = StorageType.Value(valueType)
