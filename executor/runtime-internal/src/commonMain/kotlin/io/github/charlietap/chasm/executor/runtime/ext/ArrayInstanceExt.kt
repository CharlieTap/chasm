package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.value.FieldValue

fun ArrayInstance.field(
    index: Int,
): FieldValue = try {
    this.fields[index]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.ArrayFieldLookupFailed(index))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ArrayFieldLookupFailed(index))
}
