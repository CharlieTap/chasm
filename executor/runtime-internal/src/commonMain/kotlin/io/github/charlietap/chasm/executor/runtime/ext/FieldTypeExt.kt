package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

fun FieldType.default(): ExecutionValue = when (val storageType = this.storageType) {
    is StorageType.Packed -> storageType.type.default()
    is StorageType.Value -> storageType.type.default()
}
