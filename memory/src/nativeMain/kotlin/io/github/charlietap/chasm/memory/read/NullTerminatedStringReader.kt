package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun NullTerminatedStringReader(
    memory: LinearMemory,
    memoryPointer: Int,
): String {
    val array = (memory as ByteArrayLinearMemory).memory
    var end = memoryPointer
    while (end < array.size && array[end] != 0.toByte()) end++
    return array.copyOfRange(memoryPointer, end).decodeToString()
}
