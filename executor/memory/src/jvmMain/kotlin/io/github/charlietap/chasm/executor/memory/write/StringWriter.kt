@file:JvmName("StringWriterJvm")

package io.github.charlietap.chasm.executor.memory.write

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets

actual inline fun StringWriter(
    memory: LinearMemory,
    memoryPointer: Int,
    string: String,
) {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val encoder = StandardCharsets.UTF_8.newEncoder()
    val byteBuffer = ByteBuffer.wrap(byteArray, memoryPointer, byteArray.size - memoryPointer)

    val charBuffer = CharBuffer.wrap(string)
    encoder.encode(charBuffer, byteBuffer, true)
}
