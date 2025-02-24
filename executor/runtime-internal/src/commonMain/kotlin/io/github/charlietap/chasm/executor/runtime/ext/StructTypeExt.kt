package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StructType

fun StructType.field(
    index: Index.FieldIndex,
): FieldType = try {
    fields[index.idx]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index.idx))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index.idx))
}
