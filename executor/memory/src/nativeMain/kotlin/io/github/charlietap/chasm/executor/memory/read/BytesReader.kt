package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import kotlinx.cinterop.readBytes
import liblinmem.read_bytes

actual inline fun BytesReader(
    memory: LinearMemory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int,
): ByteArray {
    val nativeMemory = memory as NativeLinearMemory

    val pointer = read_bytes(nativeMemory.pointer, memoryPointer, bytesToRead)
    if (pointer == null) {
        throw InvocationException(InvocationError.InvalidPointer)
    }

    pointer.readBytes(bytesToRead).copyInto(buffer, destinationOffset = bufferPointer)

    return buffer
}
