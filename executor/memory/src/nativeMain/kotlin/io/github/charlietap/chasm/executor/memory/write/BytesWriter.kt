package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import liblinmem.write_bytes
import platform.posix.uint8_tVar

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
    boundsChecker: BoundsChecker<Unit>,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> {

    val nativeMemory = memory as NativeLinearMemory

    buffer.usePinned { pinned ->
        val cValuesRef: CValuesRef<uint8_tVar>? = pinned.addressOf(bufferPointer).reinterpret()
        boundsChecker(memoryPointer, bytesToWrite, memorySize) {
            write_bytes(nativeMemory.pointer, memoryPointer, cValuesRef, bytesToWrite)
        }
    }

    return Ok(Unit)
}
