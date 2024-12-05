package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.ext.toDoubleLittleEndian
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceDoubleReader = (MemoryInstance, Int, Int) -> Result<Double, InvocationError.MemoryOperationOutOfBounds>

fun MemoryInstanceDoubleReader(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
): Result<Double, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceDoubleReader(
        instance = instance,
        byteOffsetInMemory = byteOffsetInMemory,
        valueSizeInBytes = valueSizeInBytes,
        linearMemoryInteractor = ::OptimisticBoundsChecker,
    )

internal inline fun MemoryInstanceDoubleReader(
    instance: MemoryInstance,
    byteOffsetInMemory: Int,
    valueSizeInBytes: Int,
    linearMemoryInteractor: OptimisticBoundsChecker<Double>,
): Result<Double, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor {
    (instance.data as ByteArrayLinearMemory).memory.toDoubleLittleEndian(byteOffsetInMemory)
}
