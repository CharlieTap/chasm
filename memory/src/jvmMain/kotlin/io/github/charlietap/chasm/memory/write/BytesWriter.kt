@file:JvmName("BytesWriterJvm")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun BytesWriter(
    memory: LinearMemory,
    memorySize: Int,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToWrite: Int,
    bufferPointer: Int,
) {
    val byteBuffer = (memory as ByteBufferLinearMemory).memory
    byteBuffer.position(memoryPointer)
    byteBuffer.put(buffer, bufferPointer, bytesToWrite)
}
