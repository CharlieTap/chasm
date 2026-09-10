@file:JvmName("StringWriterAndroid")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.charset.StandardCharsets

actual inline fun StringWriter(
    memory: LinearMemory,
    memoryPointer: Int,
    string: String,
) {
    val arrayMemory = memory as ByteArrayLinearMemory
    val bytes = string.toByteArray(StandardCharsets.UTF_8)
    BoundsChecker(memoryPointer, bytes.size, arrayMemory.byteSize)
    bytes.copyInto(arrayMemory.bytes, memoryPointer)
}
