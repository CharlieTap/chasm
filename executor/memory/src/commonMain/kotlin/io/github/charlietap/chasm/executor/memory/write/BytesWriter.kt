package io.github.charlietap.chasm.executor.memory.write

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias BytesWriter = (LinearMemory, Int, ByteArray, Int, Int, Int) -> Unit

expect inline fun BytesWriter(
    memory: LinearMemory,
    memorySize: Int,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToWrite: Int,
    bufferPointer: Int,
)
