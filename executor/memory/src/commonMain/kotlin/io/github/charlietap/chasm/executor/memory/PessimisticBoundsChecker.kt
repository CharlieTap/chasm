package io.github.charlietap.chasm.executor.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

internal typealias PessimisticBoundsChecker<T> = (Int, Int, MemoryInstance, () -> T) -> Result<T, InvocationError.MemoryOperationOutOfBounds>

internal inline fun <T> PessimisticBoundsChecker(
    address: Int,
    size: Int,
    instance: MemoryInstance,
    crossinline operation: () -> T,
): Result<T, InvocationError.MemoryOperationOutOfBounds> {
    val lastByte = address + size
    return if (address >= 0 && size >= 0 && lastByte > 0 && lastByte <= instance.size()) {
        Ok(operation())
    } else {
        Err(InvocationError.MemoryOperationOutOfBounds)
    }
}
