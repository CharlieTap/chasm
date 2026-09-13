@file:JvmName("LinearMemoryFillerJvm")

package io.github.charlietap.chasm.memory.fill

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual fun LinearMemoryFiller(
    memory: LinearMemory,
    address: Int,
    bytesToFill: Int,
    fillValue: Byte,
    upperBound: Int,
) {
    if ((bytesToFill or address) < 0 || bytesToFill > upperBound - address) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    try {
        (memory as ByteBufferLinearMemory).fill(address, fillValue, bytesToFill)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
}
