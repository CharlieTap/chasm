package io.github.charlietap.chasm.compiler.ext

import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType

internal fun FieldType.valueType(): ValueType = when (val storage = storageType) {
    is StorageType.Value -> storage.type
    is StorageType.Packed -> ValueType.Number(NumberType.I32)
}
