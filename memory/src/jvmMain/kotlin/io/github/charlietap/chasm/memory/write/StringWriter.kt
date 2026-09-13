@file:JvmName("StringWriterJvm")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun StringWriter(
    memory: LinearMemory,
    memoryPointer: Int,
    string: String,
) {
    memory.write(memoryPointer, string.encodeToByteArray())
}
