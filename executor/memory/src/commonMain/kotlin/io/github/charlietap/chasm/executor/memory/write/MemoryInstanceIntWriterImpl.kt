@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractor
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractorImpl
import io.github.charlietap.chasm.executor.memory.ext.copyInto
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

fun MemoryInstanceIntWriterImpl(
    instance: MemoryInstance,
    valueToWrite: Int,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceIntWriterImpl(
        instance = instance,
        valueToWrite = valueToWrite,
        byteOffsetInMemory = byteOffsetInMemory,
        valueSizeInBytes = valueSizeInBytes,
        linearMemoryInteractor = ::LinearMemoryInteractorImpl,
    )

internal inline fun MemoryInstanceIntWriterImpl(
    instance: MemoryInstance,
    valueToWrite: Int,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
    linearMemoryInteractor: LinearMemoryInteractor<Unit>,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(instance.data, byteOffsetInMemory, valueSizeInBytes) {
    valueToWrite.copyInto((instance.data as ByteArrayLinearMemory).memory, byteOffsetInMemory, valueSizeInBytes)
}
