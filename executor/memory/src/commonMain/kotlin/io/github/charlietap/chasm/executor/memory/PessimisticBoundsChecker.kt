package io.github.charlietap.chasm.executor.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

inline fun <T> PessimisticBoundsChecker(
    address: Int,
    bytes: Int,
    memoryUpperBound: Int,
    crossinline operation: () -> T,
): Result<T, InvocationError.MemoryOperationOutOfBounds> {
    val lastByte = address + bytes
    return if (address >= 0 && bytes >= 0 && lastByte > 0 && lastByte <= memoryUpperBound) {
        Ok(operation())
    } else {
        Err(InvocationError.MemoryOperationOutOfBounds)
    }
}
