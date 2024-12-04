@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal typealias OptimisticBoundsChecker<T> = (() -> T) -> Result<T, InvocationError.MemoryOperationOutOfBounds>

internal inline fun <T> OptimisticBoundsChecker(
    crossinline operation: () -> T,
): Result<T, InvocationError.MemoryOperationOutOfBounds> {
    return try {
        Ok(operation())
    } catch (_: IndexOutOfBoundsException) {
        Err(InvocationError.MemoryOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        Err(InvocationError.MemoryOperationOutOfBounds)
    }
}
