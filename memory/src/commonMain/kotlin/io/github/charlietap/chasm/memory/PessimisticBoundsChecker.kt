package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException

inline fun <T> PessimisticBoundsChecker(
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
