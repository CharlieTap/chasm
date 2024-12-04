@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceByteReader = (MemoryInstance, Int) -> Result<Byte, InvocationError.MemoryOperationOutOfBounds>

fun MemoryInstanceByteReader(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
): Result<Byte, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceByteReader(
        instance = instance,
        byteOffsetInMemory = byteOffsetInMemory,
        linearMemoryInteractor = ::OptimisticBoundsChecker,
    )

internal inline fun MemoryInstanceByteReader(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    linearMemoryInteractor: OptimisticBoundsChecker<Byte>,
): Result<Byte, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor {
    (instance.data as ByteArrayLinearMemory).memory[byteOffsetInMemory]
}
