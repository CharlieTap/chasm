@file:JvmName("StringReaderJvm")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun StringReader(
    memory: LinearMemory,
    memoryPointer: Int,
    stringLengthInBytes: Int,
): String {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    return byteArray.decodeToString(
        startIndex = memoryPointer,
        endIndex = memoryPointer + stringLengthInBytes,
    )
}
