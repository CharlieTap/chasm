@file:JvmName("LinearMemoryInitialiserJvm")

package io.github.charlietap.chasm.memory.init

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
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
    if (
        (srcOffset or dstOffset or bytesToInit) < 0 ||
        bytesToInit > srcUpperBound - srcOffset ||
        bytesToInit > dstUpperBound - dstOffset
    ) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val buffer = (dst as ByteBufferLinearMemory).memory
    try {
        buffer.put(dstOffset, src.asByteArray(), srcOffset, bytesToInit)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
}
