package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.ast.type.StorageType

inline fun FieldType.bitWidth() = when (val storageType = this.storageType) {
    is StorageType.Packed -> when (storageType.type) {
        is PackedType.I8 -> 8
        is PackedType.I16 -> 16
    }
    is StorageType.Value -> storageType.type.bitWidth()
}
