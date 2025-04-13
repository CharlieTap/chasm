@file:JvmName("LinearMemoryFillerJvm")

package io.github.charlietap.chasm.memory.fill

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun LinearMemoryFiller(
    memory: LinearMemory,
    address: Int,
    bytesToFill: Int,
    fillValue: Byte,
    upperBound: Int,
) {
    if (bytesToFill < 0 || address < 0 || address > upperBound - bytesToFill) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val buffer = (memory as ByteBufferLinearMemory).memory

    val slice = buffer.duplicate().apply {
        position(address)
        limit(address + bytesToFill)
    }

    repeat(bytesToFill) {
        slice.put(fillValue)
    }
}
