package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType

fun StructInstance.field(
    index: Index.FieldIndex,
): Long = try {
    this.fields[index.idx]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index.idx))
}

fun StructInstance.packedField(
    index: Index.FieldIndex,
): Pair<Long, PackedType> = try {
    val storage = this.structType.fields[index.idx].storageType as StorageType.Packed
    this.fields[index.idx] to storage.type
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index.idx))
}
