package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.StructInstance
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType

fun StructInstance.field(
    index: Index.FieldIndex,
): Long = field(index.toInt())

fun StructInstance.field(
    index: Int,
): Long = try {
    fields[index]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index))
}

fun StructInstance.packedField(
    index: Index.FieldIndex,
): Pair<Long, PackedType> = packedField(index.toInt())

fun StructInstance.packedField(
    index: Int,
): Pair<Long, PackedType> = try {
    val storage = structType.fields[index].storageType as StorageType.Packed
    fields[index] to storage.type
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index))
}
