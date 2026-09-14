package io.github.charlietap.chasm.memory.fill

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
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
    val end = address.toLong() + bytesToFill.toLong()
    if (
        (address or bytesToFill) < 0 ||
        end > upperBound.toLong() ||
        end > memory.byteSize.toLong()
    ) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    (memory as NativeMappedLinearMemory).fillUnchecked(address, fillValue, bytesToFill)
}
