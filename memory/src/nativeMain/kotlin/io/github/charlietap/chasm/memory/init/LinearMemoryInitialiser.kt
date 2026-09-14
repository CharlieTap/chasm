package io.github.charlietap.chasm.memory.init

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun LinearMemoryInitialiser(
    src: UByteArray,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    bytesToInit: Int,
    srcUpperBound: Int,
    dstUpperBound: Int,
) {
    val srcEnd = srcOffset.toLong() + bytesToInit.toLong()
    val dstEnd = dstOffset.toLong() + bytesToInit.toLong()
    if (
        (srcOffset or dstOffset or bytesToInit) < 0 ||
        srcEnd > srcUpperBound.toLong() ||
        srcEnd > src.size.toLong() ||
        dstEnd > dstUpperBound.toLong() ||
        dstEnd > dst.byteSize.toLong()
    ) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    (dst as NativeMappedLinearMemory).writeFromUnchecked(
        source = src.asByteArray(),
        sourcePointer = srcOffset,
        destinationPointer = dstOffset,
        bytesToWrite = bytesToInit,
    )
}
