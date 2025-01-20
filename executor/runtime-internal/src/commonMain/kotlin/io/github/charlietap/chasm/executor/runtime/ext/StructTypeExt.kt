package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException

fun StructType.field(
    index: Index.FieldIndex,
): FieldType = try {
    fields[index.idx.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index.idx.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index.idx.toInt()))
}
