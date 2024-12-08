@file:JvmName("BytesReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun BytesReader(
    memory: LinearMemory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int,
): Result<ByteArray, InvocationError> {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    byteArray.copyInto(buffer, bufferPointer, memoryPointer, memoryPointer + bytesToRead)
    return Ok(buffer)
}
