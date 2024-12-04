@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.ext.toIntSizedLittleEndian
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceIntReader = (MemoryInstance, Int, Int) -> Result<Int, InvocationError.MemoryOperationOutOfBounds>

fun MemoryInstanceIntReader(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
): Result<Int, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceIntReader(
        instance = instance,
        byteOffsetInMemory = byteOffsetInMemory,
        valueSizeInBytes = valueSizeInBytes,
        linearMemoryInteractor = ::OptimisticBoundsChecker,
    )

internal inline fun MemoryInstanceIntReader(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
    linearMemoryInteractor: OptimisticBoundsChecker<Int>,
): Result<Int, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor {
    (instance.data as ByteArrayLinearMemory).memory.sliceArray(
        byteOffsetInMemory until (byteOffsetInMemory + valueSizeInBytes),
    ).toIntSizedLittleEndian(valueSizeInBytes)
}
