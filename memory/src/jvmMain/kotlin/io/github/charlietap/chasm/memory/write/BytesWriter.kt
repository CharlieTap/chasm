@file:JvmName("BytesWriterJvm")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun BytesWriter(
    memory: LinearMemory,
    memorySize: Int,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToWrite: Int,
    bufferPointer: Int,
) {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    buffer.copyInto(byteArray, memoryPointer, bufferPointer, bufferPointer + bytesToWrite)
}
