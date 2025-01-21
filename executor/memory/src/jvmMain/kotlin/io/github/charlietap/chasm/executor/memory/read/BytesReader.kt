@file:JvmName("BytesReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun BytesReader(
    memory: LinearMemory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int,
): ByteArray {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    byteArray.copyInto(buffer, bufferPointer, memoryPointer, memoryPointer + bytesToRead)
    return buffer
}
