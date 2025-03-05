package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlinx.cinterop.readBytes
import liblinmem.read_bytes

actual inline fun StringReader(
    memory: LinearMemory,
    memoryPointer: Int,
    stringLengthInBytes: Int,
): String {
    val nativeMemory = memory as NativeLinearMemory

    val pointer = read_bytes(nativeMemory.pointer, memoryPointer, stringLengthInBytes)
    if (pointer == null) {
        throw InvocationException(InvocationError.InvalidPointer)
    }

    return pointer.readBytes(stringLengthInBytes).decodeToString()
}
