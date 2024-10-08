@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

internal inline fun <T> LinearMemoryInteractorImpl(
    memory: LinearMemory,
    offset: Int,
    size: Int,
    crossinline operation: () -> T,
): Result<T, InvocationError.MemoryOperationOutOfBounds> {
    val lastByte = offset + size

    return if (offset >= 0 && size >= 0 && lastByte > 0 && lastByte <= memory.size()) {
        try {
            Ok(operation())
        } catch (_: IndexOutOfBoundsException) {
            Err(InvocationError.MemoryOperationOutOfBounds)
        }
    } else {
        Err(InvocationError.MemoryOperationOutOfBounds)
    }
}
