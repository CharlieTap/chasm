@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractor
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractorImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

fun MemoryInstanceByteWriterImpl(
    instance: MemoryInstance,
    valueToWrite: Byte,
    byteOffsetInMemory: Int,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceByteWriterImpl(
        instance = instance,
        valueToWrite = valueToWrite,
        byteOffsetInMemory = byteOffsetInMemory,
        linearMemoryInteractor = ::LinearMemoryInteractorImpl,
    )

internal inline fun MemoryInstanceByteWriterImpl(
    instance: MemoryInstance,
    valueToWrite: Byte,
    byteOffsetInMemory: Int,
    linearMemoryInteractor: LinearMemoryInteractor<Unit>,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(instance.data, byteOffsetInMemory, 1) {
    (instance.data as ByteArrayLinearMemory).memory[byteOffsetInMemory] = valueToWrite
}
