package io.github.charlietap.chasm.memory.init

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun LinearMemoryLoader(
    dst: LinearMemory,
    dstOffset: Int,
    bytesToLoad: Int,
    dstUpperBound: Int,
    load: (ByteArray, Int) -> Unit,
) {
    val dstEnd = dstOffset.toLong() + bytesToLoad.toLong()
    if (
        (dstOffset or bytesToLoad) < 0 ||
        dstEnd > dstUpperBound.toLong() ||
        dstEnd > dst.byteSize.toLong()
    ) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
    val bytes = ByteArray(bytesToLoad)
    load(bytes, 0)
    (dst as NativeMappedLinearMemory).writeFromUnchecked(
        source = bytes,
        sourcePointer = 0,
        destinationPointer = dstOffset,
        bytesToWrite = bytesToLoad,
    )
}
