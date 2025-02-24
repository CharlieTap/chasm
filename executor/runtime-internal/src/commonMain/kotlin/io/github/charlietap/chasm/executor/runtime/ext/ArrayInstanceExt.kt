package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType

fun ArrayInstance.field(
    index: Int,
): Long = try {
    this.fields[index]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.ArrayFieldLookupFailed(index))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ArrayFieldLookupFailed(index))
}

fun ArrayInstance.packedField(
    index: Int,
): Pair<Long, PackedType> = try {
    val storage = this.arrayType.fieldType.storageType as StorageType.Packed
    this.fields[index] to storage.type
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.ArrayFieldLookupFailed(index))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ArrayFieldLookupFailed(index))
}
