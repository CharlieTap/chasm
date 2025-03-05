package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException

actual inline fun <T> OptimisticBoundsChecker(
    address: Int,
    bytes: Int,
    memoryUpperBound: Int,
    crossinline operation: () -> T,
): T {
    return try {
        operation()
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
}
