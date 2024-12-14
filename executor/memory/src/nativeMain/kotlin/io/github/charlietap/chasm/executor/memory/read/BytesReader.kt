package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import kotlinx.cinterop.get
import liblinmem.read_bytes

actual inline fun BytesReader(
    memory: LinearMemory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int,
): Result<ByteArray, InvocationError> {
    val nativeMemory = memory as NativeLinearMemory

    val pointer = read_bytes(nativeMemory.pointer, memoryPointer, bytesToRead)
    if (pointer == null) {
        return Err(InvocationError.InvalidPointer)
    }

    for (i in 0 until bytesToRead) {
        buffer[bufferPointer + i] = pointer[i].toByte()
    }

    return Ok(buffer)
}
