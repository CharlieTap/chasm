package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceBytesWriter = (MemoryInstance, Int, ByteArray) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>

fun MemoryInstanceBytesWriter(
    instance: MemoryInstance,
    address: Int,
    bytes: ByteArray,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceBytesWriter(
        instance = instance,
        address = address,
        bytes = bytes,
        linearMemoryInteractor = ::PessimisticBoundsChecker,
    )

internal inline fun MemoryInstanceBytesWriter(
    instance: MemoryInstance,
    address: Int,
    bytes: ByteArray,
    linearMemoryInteractor: PessimisticBoundsChecker<Unit>,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(address, bytes.size, instance) {
    bytes.copyInto((instance.data as ByteArrayLinearMemory).memory, address)
}
