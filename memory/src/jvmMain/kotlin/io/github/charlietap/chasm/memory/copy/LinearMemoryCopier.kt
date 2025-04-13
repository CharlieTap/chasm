@file:JvmName("LinearMemoryCopierJvm")

package io.github.charlietap.chasm.memory.copy

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.BufferOverflowException
import java.nio.BufferUnderflowException

actual inline fun LinearMemoryCopier(
    src: LinearMemory,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    copySize: Int,
    srcUpperBound: Int,
    dstUpperBound: Int,
) {
    if (copySize < 0 || srcOffset < 0 || dstOffset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val srcBuffer = (src as ByteBufferLinearMemory).memory
    val dstBuffer = (dst as ByteBufferLinearMemory).memory

    try {
        val srcSlice = srcBuffer.duplicate().apply {
            position(srcOffset)
            limit(srcOffset + copySize)
        }

        val dstSlice = dstBuffer.duplicate().apply {
            position(dstOffset)
            limit(dstOffset + copySize)
        }

        dstSlice.put(srcSlice)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    } catch (_: BufferOverflowException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    } catch (_: BufferUnderflowException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
}
