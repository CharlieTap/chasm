package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.ext.toULongSizedLittleEndian
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceULongReader = (MemoryInstance, Int, Int) -> Result<ULong, InvocationError.MemoryOperationOutOfBounds>

fun MemoryInstanceULongReader(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
): Result<ULong, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceULongReader(
        instance = instance,
        byteOffsetInMemory = byteOffsetInMemory,
        valueSizeInBytes = valueSizeInBytes,
        linearMemoryInteractor = ::OptimisticBoundsChecker,
    )

internal inline fun MemoryInstanceULongReader(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
    linearMemoryInteractor: OptimisticBoundsChecker<ULong>,
): Result<ULong, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor {
    (instance.data as ByteArrayLinearMemory).memory.sliceArray(
        byteOffsetInMemory until (byteOffsetInMemory + valueSizeInBytes),
    ).toULongSizedLittleEndian(valueSizeInBytes)
}
