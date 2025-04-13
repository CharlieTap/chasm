@file:JvmName("NullTerminatedStringReaderJvm")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

actual inline fun NullTerminatedStringReader(
    memory: LinearMemory,
    memoryPointer: Int,
): String {
    val buffer = (memory as ByteBufferLinearMemory).memory
    val length = findNull(buffer, memoryPointer)
    if (length == -1) return ""

    val bytes = ByteArray(length)

    buffer.position(memoryPointer)
    buffer.limit(memoryPointer + length)
    buffer.get(bytes, 0, length)

    return String(bytes, StandardCharsets.UTF_8)
}

inline fun findNull(buffer: ByteBuffer, startIndex: Int): Int {
    val limit = buffer.limit()
    var index = startIndex

    while (index + 7 < limit) {
        val word = buffer.getLong(index)
        if (word.containsNullByte()) {
            for (i in 0 until 8) {
                if (buffer.get(index + i) == 0.toByte()) {
                    return index + i - startIndex
                }
            }
        }
        index += 8
    }

    while (index < limit) {
        if (buffer.get(index) == 0.toByte()) {
            return index - startIndex
        }
        index++
    }

    return -1
}

inline fun Long.containsNullByte(): Boolean {
    return ((this - 0x0101010101010101L) and this.inv() and -0x7f7f7f7f7f7f7f80L) != 0L
}
