@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractor
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractorImpl
import io.github.charlietap.chasm.executor.memory.ext.toDoubleLittleEndian
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

fun MemoryInstanceDoubleReaderImpl(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
): Result<Double, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceDoubleReaderImpl(
        instance = instance,
        byteOffsetInMemory = byteOffsetInMemory,
        valueSizeInBytes = valueSizeInBytes,
        linearMemoryInteractor = ::LinearMemoryInteractorImpl,
    )

internal inline fun MemoryInstanceDoubleReaderImpl(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
    linearMemoryInteractor: LinearMemoryInteractor<Double>,
): Result<Double, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(
    instance.data,
    byteOffsetInMemory,
    valueSizeInBytes,
) {
    (instance.data as ByteArrayLinearMemory).memory.toDoubleLittleEndian(byteOffsetInMemory)
}
