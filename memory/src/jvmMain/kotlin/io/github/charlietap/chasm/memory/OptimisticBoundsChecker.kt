package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import java.nio.BufferOverflowException
import java.nio.BufferUnderflowException

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
    } catch (_: BufferOverflowException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    } catch (_: BufferUnderflowException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
}
