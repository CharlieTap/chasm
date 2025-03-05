package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.TableInstance

inline fun TableInstance.element(
    elementIndex: Int,
): Long = try {
    elements[elementIndex]
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.TableElementLookupFailed(elementIndex))
}
