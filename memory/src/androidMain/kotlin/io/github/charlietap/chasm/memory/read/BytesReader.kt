@file:JvmName("BytesReaderAndroid")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun BytesReader(
    memory: LinearMemory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int,
): ByteArray {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(memoryPointer, bytesToRead, arrayMemory.byteSize)
    BoundsChecker(bufferPointer, bytesToRead, buffer.size)
    arrayMemory.bytes.copyInto(buffer, bufferPointer, memoryPointer, memoryPointer + bytesToRead)
    return buffer
}
