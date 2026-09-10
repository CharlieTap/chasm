@file:JvmName("BytesWriterAndroid")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.BoundsChecker
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
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(memoryPointer, bytesToWrite, arrayMemory.byteSize)
    BoundsChecker(bufferPointer, bytesToWrite, buffer.size)
    buffer.copyInto(arrayMemory.bytes, memoryPointer, bufferPointer, bufferPointer + bytesToWrite)
}
