package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun StringReader(
    memory: LinearMemory,
    memoryPointer: Int,
    stringLengthInBytes: Int,
): String {
    val array = (memory as ByteArrayLinearMemory).memory
    return array.copyOfRange(memoryPointer, memoryPointer + stringLengthInBytes).decodeToString()
}
