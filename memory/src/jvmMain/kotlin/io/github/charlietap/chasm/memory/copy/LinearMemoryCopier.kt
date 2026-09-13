@file:JvmName("LinearMemoryCopierJvm")

package io.github.charlietap.chasm.memory.copy

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun LinearMemoryCopier(
    src: LinearMemory,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    copySize: Int,
    srcUpperBound: Int,
    dstUpperBound: Int,
) {
    if (
        (copySize or srcOffset or dstOffset) < 0 ||
        copySize > srcUpperBound - srcOffset ||
        copySize > dstUpperBound - dstOffset
    ) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    try {
        (dst as ByteBufferLinearMemory).move(
            sourcePointer = srcOffset,
            destinationPointer = dstOffset,
            bytesToMove = copySize,
            source = src,
        )
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
}
