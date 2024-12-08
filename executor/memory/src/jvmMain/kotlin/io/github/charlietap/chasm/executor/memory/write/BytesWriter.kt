@file:JvmName("BytesWriterJvm")

package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun BytesWriter(
    memory: LinearMemory,
    memorySize: Int,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToWrite: Int,
    bufferPointer: Int,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
    BytesWriter(
        memory = memory,
        memorySize = memorySize,
        buffer = buffer,
        memoryPointer = memoryPointer,
        bytesToWrite = bytesToWrite,
        bufferPointer = bufferPointer,
        boundsChecker = ::OptimisticBoundsChecker,
    )

inline fun BytesWriter(
    memory: LinearMemory,
    memorySize: Int,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToWrite: Int,
    bufferPointer: Int,
    boundsChecker: BoundsChecker<ByteArray>,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> {

    val byteArray = (memory as ByteArrayLinearMemory).memory
    boundsChecker(memoryPointer, bytesToWrite, memorySize) {
        buffer.copyInto(byteArray, memoryPointer, bufferPointer, bufferPointer + bytesToWrite)
    }
    return Ok(Unit)
}
