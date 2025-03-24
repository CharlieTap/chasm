package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StorageType

fun FieldType.default(): Long = when (val storageType = this.storageType) {
    is StorageType.Packed -> storageType.type.default()
    is StorageType.Value -> storageType.type.default()
}
