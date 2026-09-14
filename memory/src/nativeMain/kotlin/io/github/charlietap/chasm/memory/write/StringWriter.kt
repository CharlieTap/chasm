package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun StringWriter(
    memory: LinearMemory,
    memoryPointer: Int,
    string: String,
) {
    val bytes = string.encodeToByteArray()
    memory.write(memoryPointer, bytes)
}
