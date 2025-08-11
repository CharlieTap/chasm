package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun BytesReader(
    memory: LinearMemory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int,
): ByteArray {
    val array = (memory as ByteArrayLinearMemory).memory
    array.copyInto(buffer, bufferPointer, memoryPointer, memoryPointer + bytesToRead)
    return buffer
}
