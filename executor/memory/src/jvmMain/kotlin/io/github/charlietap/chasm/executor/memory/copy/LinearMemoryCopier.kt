@file:JvmName("LinearMemoryCopierJvm")

package io.github.charlietap.chasm.executor.memory.copy

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun LinearMemoryCopier(
    src: LinearMemory,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    copySize: Int,
    srcUpperBound: Int,
    dstUpperBound: Int,
) {
    val srcByteArray = (src as ByteArrayLinearMemory).memory
    val dstByteArray = (dst as ByteArrayLinearMemory).memory

    try {
        srcByteArray.copyInto(dstByteArray, dstOffset, srcOffset, srcOffset + copySize)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
}
