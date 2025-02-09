package io.github.charlietap.chasm.executor.memory

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException

actual inline fun <T> OptimisticBoundsChecker(
    address: Int,
    bytes: Int,
    memoryUpperBound: Int,
    crossinline operation: () -> T,
): T {
    val lastByte = address + bytes
    return if (address >= 0 && lastByte >= address && lastByte <= memoryUpperBound) {
        operation()
    } else {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
}
