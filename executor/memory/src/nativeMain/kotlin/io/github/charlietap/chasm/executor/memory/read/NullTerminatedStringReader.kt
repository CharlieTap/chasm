package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlinx.cinterop.readBytes
import liblinmem.find_null
import liblinmem.read_bytes

actual inline fun NullTerminatedStringReader(
    memory: LinearMemory,
    memoryPointer: Int,
): String {
    val nativeMemory = memory as NativeLinearMemory

    val length = find_null(nativeMemory.pointer, memoryPointer) - 1
    val pointer = read_bytes(nativeMemory.pointer, memoryPointer, length)
    if (pointer == null) {
        throw InvocationException(InvocationError.InvalidPointer)
    }

    return pointer.readBytes(length).decodeToString()
}
