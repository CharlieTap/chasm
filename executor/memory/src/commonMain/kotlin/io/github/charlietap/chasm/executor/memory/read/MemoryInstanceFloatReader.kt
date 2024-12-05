package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.ext.toFloatLittleEndian
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceFloatReader = (MemoryInstance, Int, Int) -> Result<Float, InvocationError.MemoryOperationOutOfBounds>

fun MemoryInstanceFloatReader(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
): Result<Float, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceFloatReader(
        instance = instance,
        byteOffsetInMemory = byteOffsetInMemory,
        valueSizeInBytes = valueSizeInBytes,
        linearMemoryInteractor = ::OptimisticBoundsChecker,
    )

internal inline fun MemoryInstanceFloatReader(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
    linearMemoryInteractor: OptimisticBoundsChecker<Float>,
): Result<Float, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor {
    (instance.data as ByteArrayLinearMemory).memory.toFloatLittleEndian(byteOffsetInMemory)
}
