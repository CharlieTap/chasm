@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.ext.copyInto
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceLongWriter = (MemoryInstance, Long, Int, Int) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>

fun MemoryInstanceLongWriter(
    instance: MemoryInstance,
    valueToWrite: Long,
    address: Int,
    valueSizeInBytes: Int,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceLongWriter(
        instance = instance,
        valueToWrite = valueToWrite,
        address = address,
        valueSizeInBytes = valueSizeInBytes,
        linearMemoryInteractor = ::PessimisticBoundsChecker,
    )

internal inline fun MemoryInstanceLongWriter(
    instance: MemoryInstance,
    valueToWrite: Long,
    address: Int,
    valueSizeInBytes: Int,
    linearMemoryInteractor: PessimisticBoundsChecker<Unit>,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(address, valueSizeInBytes, instance) {
    valueToWrite.copyInto((instance.data as ByteArrayLinearMemory).memory, address, valueSizeInBytes)
}
