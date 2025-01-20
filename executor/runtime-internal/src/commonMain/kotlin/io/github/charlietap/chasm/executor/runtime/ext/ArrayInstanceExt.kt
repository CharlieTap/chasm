package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.value.FieldValue

fun ArrayInstance.field(
    index: Index.FieldIndex,
): FieldValue = try {
    this.fields[index.idx.toInt()]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.ArrayFieldLookupFailed(index.idx.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ArrayFieldLookupFailed(index.idx.toInt()))
}
