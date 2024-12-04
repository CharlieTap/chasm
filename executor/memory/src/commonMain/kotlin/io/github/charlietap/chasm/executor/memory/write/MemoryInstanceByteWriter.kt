@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceByteWriter = (MemoryInstance, Int, Byte) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>

fun MemoryInstanceByteWriter(
    instance: MemoryInstance,
    address: Int,
    byte: Byte,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
    MemoryInstanceByteWriter(
        instance = instance,
        address = address,
        byte = byte,
        linearMemoryInteractor = ::PessimisticBoundsChecker,
    )

internal inline fun MemoryInstanceByteWriter(
    instance: MemoryInstance,
    address: Int,
    byte: Byte,
    linearMemoryInteractor: PessimisticBoundsChecker<Unit>,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = linearMemoryInteractor(address, 1, instance) {
    (instance.data as ByteArrayLinearMemory).memory[address] = byte
}
