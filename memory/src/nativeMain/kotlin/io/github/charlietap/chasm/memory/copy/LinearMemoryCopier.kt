package io.github.charlietap.chasm.memory.copy

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
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
    val srcEnd = srcOffset.toLong() + copySize.toLong()
    val dstEnd = dstOffset.toLong() + copySize.toLong()
    if (
        (srcOffset or dstOffset or copySize) < 0 ||
        srcEnd > srcUpperBound.toLong() ||
        srcEnd > src.byteSize.toLong() ||
        dstEnd > dstUpperBound.toLong() ||
        dstEnd > dst.byteSize.toLong()
    ) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    (dst as NativeMappedLinearMemory).moveFromUnchecked(
        source = src as NativeMappedLinearMemory,
        sourcePointer = srcOffset,
        destinationPointer = dstOffset,
        bytesToMove = copySize,
    )
}
