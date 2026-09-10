@file:JvmName("LinearMemoryFillerAndroid")

package io.github.charlietap.chasm.memory.fill

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
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
        val arrayMemory = memory as ByteArrayLinearMemory
        BoundsChecker(address, bytesToFill, arrayMemory.byteSize)
        val bytes = arrayMemory.bytes
        var filled = minOf(64, bytesToFill)
        bytes.fill(fillValue, address, address + filled)
        while (filled < bytesToFill) {
            val count = minOf(filled, bytesToFill - filled)
            bytes.copyInto(bytes, address + filled, address, address + count)
            filled += count
        }
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
}
