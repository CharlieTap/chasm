@file:JvmName("BytesReaderJvm")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun BytesReader(
    memory: LinearMemory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int,
): ByteArray {
    val byteBuffer = (memory as ByteBufferLinearMemory).memory

    byteBuffer.position(memoryPointer)
    byteBuffer.get(buffer, 0, bytesToRead)

    return buffer
}
