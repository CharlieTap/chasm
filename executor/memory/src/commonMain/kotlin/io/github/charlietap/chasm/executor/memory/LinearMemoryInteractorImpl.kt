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

    return if (lastByte > 0 && lastByte <= memory.size()) {
        Ok(operation())
    } else {
        Err(InvocationError.MemoryOperationOutOfBounds)
    }
}
