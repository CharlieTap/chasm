@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractor
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractorImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

fun MemoryInstanceByteReaderImpl(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
): Result<Byte, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceByteReaderImpl(
        instance = instance,
        byteOffsetInMemory = byteOffsetInMemory,
        linearMemoryInteractor = ::LinearMemoryInteractorImpl,
    )

internal inline fun MemoryInstanceByteReaderImpl(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    linearMemoryInteractor: LinearMemoryInteractor<Byte>,
): Result<Byte, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(instance.data, byteOffsetInMemory, 1) {
    (instance.data as ByteArrayLinearMemory).memory[byteOffsetInMemory]
}
