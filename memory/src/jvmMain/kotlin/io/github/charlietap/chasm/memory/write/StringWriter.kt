@file:JvmName("StringWriterJvm")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets

actual inline fun StringWriter(
    memory: LinearMemory,
    memoryPointer: Int,
    string: String,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    buffer.position(memoryPointer)
    val encoder = StandardCharsets.UTF_8.newEncoder()
    val charBuffer = CharBuffer.wrap(string)
    encoder.encode(charBuffer, buffer, true)
}
