package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.memory.checkedAddress
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun NullTerminatedStringReader(
    memory: LinearMemory,
    memoryPointer: Int,
): String {
    val nativeMemory = memory as NativeMappedLinearMemory
    checkedAddress(memoryPointer, 0, nativeMemory.byteSize)
    var end = memoryPointer
    while (end < nativeMemory.byteSize && nativeMemory.loadI8Unchecked(end) != 0.toByte()) end++
    val bytes = ByteArray(end - memoryPointer)
    nativeMemory.read(bytes, memoryPointer, bytes.size)
    return bytes.decodeToString()
}
