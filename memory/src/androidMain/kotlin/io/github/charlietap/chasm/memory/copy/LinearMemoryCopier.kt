@file:JvmName("LinearMemoryCopierAndroid")

package io.github.charlietap.chasm.memory.copy

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
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
        val source = src as ByteArrayLinearMemory
        val destination = dst as ByteArrayLinearMemory
        BoundsChecker(srcOffset, copySize, source.byteSize)
        BoundsChecker(dstOffset, copySize, destination.byteSize)
        source.bytes.copyInto(destination.bytes, dstOffset, srcOffset, srcOffset + copySize)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
}
