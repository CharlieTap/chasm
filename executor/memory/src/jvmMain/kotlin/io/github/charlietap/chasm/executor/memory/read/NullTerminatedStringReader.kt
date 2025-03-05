@file:JvmName("NullTerminatedStringReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual inline fun NullTerminatedStringReader(
    memory: LinearMemory,
    memoryPointer: Int,
): String {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val length = findNull(byteArray, memoryPointer) - 1
    return byteArray.decodeToString(
        startIndex = memoryPointer,
        endIndex = memoryPointer + length,
    )
}

inline fun findNull(memory: ByteArray, pointer: Int): Int {
    val size = memory.size
    var index = pointer

    val buffer = ByteBuffer.wrap(memory).order(ByteOrder.LITTLE_ENDIAN)

    while (index + 7 < size) {
        val word = buffer.getLong(index)
        if (word.containsNullByte()) {
            for (i in 0 until 8) {
                if (memory[index + i] == 0.toByte()) {
                    return index + i
                }
            }
        }
        index += 8
    }

    while (index < size) {
        if (memory[index] == 0.toByte()) {
            return index
        }
        index++
    }

    return -1
}

inline fun Long.containsNullByte(): Boolean {
    return ((this - 0x0101010101010101L) and this.inv() and -0x7f7f7f7f7f7f7f80L) != 0L
}
