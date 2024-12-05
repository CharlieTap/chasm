package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.ext.toLongSizedLittleEndian
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceLongReader = (MemoryInstance, Int, Int) -> Result<Long, InvocationError.MemoryOperationOutOfBounds>

fun MemoryInstanceLongReader(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
): Result<Long, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceLongReader(
        instance = instance,
        byteOffsetInMemory = byteOffsetInMemory,
        valueSizeInBytes = valueSizeInBytes,
        linearMemoryInteractor = ::OptimisticBoundsChecker,
    )

internal inline fun MemoryInstanceLongReader(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
    linearMemoryInteractor: OptimisticBoundsChecker<Long>,
): Result<Long, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor {
    (instance.data as ByteArrayLinearMemory).memory.sliceArray(
        byteOffsetInMemory until (byteOffsetInMemory + valueSizeInBytes),
    ).toLongSizedLittleEndian(valueSizeInBytes)
}
