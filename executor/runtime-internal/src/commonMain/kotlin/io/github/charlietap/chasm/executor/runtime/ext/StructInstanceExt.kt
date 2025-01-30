package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance

fun StructInstance.field(
    index: Index.FieldIndex,
): Long = try {
    this.fields[index.idx.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index.idx.toInt()))
}

fun StructInstance.packedField(
    index: Index.FieldIndex,
): Pair<Long, PackedType> = try {
    val storage = this.structType.fields[index.idx.toInt()].storageType as StorageType.Packed
    this.fields[index.idx.toInt()] to storage.type
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index.idx.toInt()))
}
