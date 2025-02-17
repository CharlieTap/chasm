package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ir.type.FieldType
import io.github.charlietap.chasm.ir.type.StorageType
import io.github.charlietap.chasm.type.ir.matching.TypeMatcherContext

fun FieldType.default(
    context: TypeMatcherContext,
): Long = when (val storageType = this.storageType) {
    is StorageType.Packed -> storageType.type.default()
    is StorageType.Value -> storageType.type.default(context)
}
