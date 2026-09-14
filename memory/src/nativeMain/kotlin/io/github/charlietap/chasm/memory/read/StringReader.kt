package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun StringReader(
    memory: LinearMemory,
    memoryPointer: Int,
    stringLengthInBytes: Int,
): String {
    val bytes = ByteArray(stringLengthInBytes)
    memory.read(bytes, memoryPointer, stringLengthInBytes)
    return bytes.decodeToString()
}
