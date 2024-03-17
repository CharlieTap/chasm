@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractor
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractorImpl
import io.github.charlietap.chasm.executor.memory.ext.toLongLittleEndian
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

fun MemoryInstanceLongReaderImpl(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
): Result<Long, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceLongReaderImpl(
        instance = instance,
        byteOffsetInMemory = byteOffsetInMemory,
        valueSizeInBytes = valueSizeInBytes,
        linearMemoryInteractor = ::LinearMemoryInteractorImpl,
    )

internal inline fun MemoryInstanceLongReaderImpl(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
    linearMemoryInteractor: LinearMemoryInteractor<Long>,
): Result<Long, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(instance.data, byteOffsetInMemory, valueSizeInBytes) {
    (instance.data as ByteArrayLinearMemory).memory.sliceArray(
        byteOffsetInMemory..(byteOffsetInMemory + valueSizeInBytes),
    ).toLongLittleEndian()
}
