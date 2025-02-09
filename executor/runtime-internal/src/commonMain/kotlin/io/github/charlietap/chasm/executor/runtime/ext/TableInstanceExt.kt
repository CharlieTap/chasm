package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance

inline fun TableInstance.element(
    elementIndex: Int,
): Long = try {
    elements[elementIndex]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.TableElementLookupFailed(elementIndex))
}
