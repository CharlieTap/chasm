package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.type.matching.TypeMatcherContext

fun FieldType.default(
    context: TypeMatcherContext,
): Long = when (val storageType = this.storageType) {
    is StorageType.Packed -> storageType.type.default()
    is StorageType.Value -> storageType.type.default(context)
}
