package io.github.charlietap.chasm.executor.memory.copy

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.copy

actual inline fun LinearMemoryCopier(
    src: LinearMemory,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    copySize: Int,
    srcUpperBound: Int,
    dstUpperBound: Int,
) {
    val srcMemory = (src as NativeLinearMemory)
    val dstMemory = (dst as NativeLinearMemory)

    if (
        copySize < 0 ||
        srcOffset < 0 ||
        (srcOffset + copySize) > srcUpperBound ||
        dstOffset < 0 ||
        (dstOffset + copySize) > dstUpperBound
    ) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    copy(srcMemory.pointer, dstMemory.pointer, srcOffset, dstOffset, copySize)
}
