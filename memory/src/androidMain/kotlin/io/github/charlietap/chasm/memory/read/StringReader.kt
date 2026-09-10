@file:JvmName("StringReaderAndroid")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.charset.StandardCharsets

actual inline fun StringReader(
    memory: LinearMemory,
    memoryPointer: Int,
    stringLengthInBytes: Int,
): String {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(memoryPointer, stringLengthInBytes, arrayMemory.byteSize)
    return String(arrayMemory.bytes, memoryPointer, stringLengthInBytes, StandardCharsets.UTF_8)
}
