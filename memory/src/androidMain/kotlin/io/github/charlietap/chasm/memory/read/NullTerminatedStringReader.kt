@file:JvmName("NullTerminatedStringReaderAndroid")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.charset.StandardCharsets

actual inline fun NullTerminatedStringReader(
    memory: LinearMemory,
    memoryPointer: Int,
): String {
    val arrayMemory = memory as ByteArrayLinearMemory
    val size = arrayMemory.byteSize
    BoundsChecker(memoryPointer, 0, size)
    val bytes = arrayMemory.bytes
    var end = memoryPointer
    while (end < size && bytes[end] != 0.toByte()) end++
    if (end == size) return ""
    return String(bytes, memoryPointer, end - memoryPointer, StandardCharsets.UTF_8)
}
