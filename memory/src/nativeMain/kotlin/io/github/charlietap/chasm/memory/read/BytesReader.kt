package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun BytesReader(
    memory: LinearMemory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int,
): ByteArray {
    return memory.read(buffer, memoryPointer, bytesToRead, bufferPointer)
}
