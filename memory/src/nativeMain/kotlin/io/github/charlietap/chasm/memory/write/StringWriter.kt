package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun StringWriter(
    memory: LinearMemory,
    memoryPointer: Int,
    string: String,
) {
    val array = (memory as ByteArrayLinearMemory).memory
    val bytes = string.encodeToByteArray()
    bytes.copyInto(array, destinationOffset = memoryPointer)
}
