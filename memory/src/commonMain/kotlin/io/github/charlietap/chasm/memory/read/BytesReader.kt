package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias BytesReader = (LinearMemory, ByteArray, Int, Int, Int) -> ByteArray

expect inline fun BytesReader(
    memory: LinearMemory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int,
): ByteArray
