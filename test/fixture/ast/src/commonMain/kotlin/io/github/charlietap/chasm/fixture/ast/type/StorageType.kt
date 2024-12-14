package io.github.charlietap.chasm.fixture.ast.type

import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.ast.type.ValueType

fun storageType() = packedStorageType()

fun packedStorageType(
    packedType: PackedType = packedType(),
) = StorageType.Packed(packedType)

fun valueStorageType(
    valueType: ValueType = valueType(),
) = StorageType.Value(valueType)
