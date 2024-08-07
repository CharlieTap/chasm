@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractor
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractorImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

fun MemoryInstanceBytesReaderImpl(
    instance: MemoryInstance,
    pointer: Int,
    numberOfBytes: Int,
): Result<ByteArray, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceBytesReaderImpl(
        instance = instance,
        pointer = pointer,
        numberOfBytes = numberOfBytes,
        linearMemoryInteractor = ::LinearMemoryInteractorImpl,
    )

internal inline fun MemoryInstanceBytesReaderImpl(
    instance: MemoryInstance,
    pointer: Int,
    numberOfBytes: Int,
    linearMemoryInteractor: LinearMemoryInteractor<ByteArray>,
): Result<ByteArray, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(instance.data, pointer, numberOfBytes) {
    (instance.data as ByteArrayLinearMemory).memory.sliceArray(pointer until pointer + numberOfBytes)
}
