@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractor
import io.github.charlietap.chasm.executor.memory.LinearMemoryInteractorImpl
import io.github.charlietap.chasm.executor.memory.ext.toIntLittleEndian
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

fun MemoryInstanceIntReaderImpl(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
): Result<Int, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceIntReaderImpl(
        instance = instance,
        byteOffsetInMemory = byteOffsetInMemory,
        valueSizeInBytes = valueSizeInBytes,
        linearMemoryInteractor = ::LinearMemoryInteractorImpl,
    )

internal inline fun MemoryInstanceIntReaderImpl(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
    linearMemoryInteractor: LinearMemoryInteractor<Int>,
): Result<Int, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(instance.data, byteOffsetInMemory, valueSizeInBytes) {
    (instance.data as ByteArrayLinearMemory).memory.sliceArray(
        byteOffsetInMemory..(byteOffsetInMemory + valueSizeInBytes),
    ).toIntLittleEndian()
}
