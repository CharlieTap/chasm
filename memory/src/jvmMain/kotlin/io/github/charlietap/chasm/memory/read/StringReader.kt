@file:JvmName("StringReaderJvm")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.charset.StandardCharsets

actual inline fun StringReader(
    memory: LinearMemory,
    memoryPointer: Int,
    stringLengthInBytes: Int,
): String {
    val buffer = (memory as ByteBufferLinearMemory).memory
    val bytes = ByteArray(stringLengthInBytes)

    buffer.position(memoryPointer)
    buffer.get(bytes, 0, stringLengthInBytes)

    return String(bytes, StandardCharsets.UTF_8)
}
