@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

/*
    On the JVM we perform no bounds check and simply set up a try catch
    whereas on native platforms we always perform bounds checks
 */
expect inline fun <T> OptimisticBoundsChecker(
    address: Int,
    bytes: Int,
    memoryUpperBound: Int,
    crossinline operation: () -> T,
): Result<T, InvocationError.MemoryOperationOutOfBounds>
