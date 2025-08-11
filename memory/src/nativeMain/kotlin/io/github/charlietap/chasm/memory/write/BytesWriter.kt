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
    val array = (memory as ByteArrayLinearMemory).memory
    buffer.copyInto(
        destination = array,
        destinationOffset = memoryPointer,
        startIndex = bufferPointer,
        endIndex = bufferPointer + bytesToWrite,
    )
}
