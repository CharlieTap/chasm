@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractor
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractorImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceBytesReader = (MemoryInstance, ByteArray, Int, Int, Int) -> Result<ByteArray, InvocationError.MemoryOperationOutOfBounds>

fun MemoryInstanceBytesReader(
    instance: MemoryInstance,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int,
): Result<ByteArray, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceBytesReader(
        instance = instance,
        buffer = buffer,
        memoryPointer = memoryPointer,
        bytesToRead = bytesToRead,
        bufferPointer = bufferPointer,
        linearMemoryInteractor = ::LinearMemoryInteractorImpl,
    )

internal inline fun MemoryInstanceBytesReader(
    instance: MemoryInstance,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int,
    linearMemoryInteractor: LinearMemoryInteractor<ByteArray>,
): Result<ByteArray, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(instance.data, memoryPointer, bytesToRead) {
    (instance.data as ByteArrayLinearMemory).memory.copyInto(buffer, bufferPointer, memoryPointer, memoryPointer + bytesToRead)
}
