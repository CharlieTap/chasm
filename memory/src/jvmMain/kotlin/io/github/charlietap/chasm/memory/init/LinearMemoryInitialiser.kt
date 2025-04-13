@file:JvmName("LinearMemoryInitialiserJvm")

package io.github.charlietap.chasm.memory.init

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.BufferOverflowException
import java.nio.BufferUnderflowException

actual inline fun LinearMemoryInitialiser(
    src: UByteArray,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    bytesToInit: Int,
    srcUpperBound: Int,
    dstUpperBound: Int,
) {
    val buffer = (dst as ByteBufferLinearMemory).memory
    try {
        buffer.position(dstOffset)
        val byteArray = src.asByteArray()
        buffer.put(byteArray, srcOffset, bytesToInit)
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
