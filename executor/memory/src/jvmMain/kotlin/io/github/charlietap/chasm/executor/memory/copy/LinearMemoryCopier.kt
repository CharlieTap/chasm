@file:JvmName("LinearMemoryCopierJvm")

package io.github.charlietap.chasm.executor.memory.copy

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun LinearMemoryCopier(
    src: LinearMemory,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    copySize: Int,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> {

    val srcByteArray = (src as ByteArrayLinearMemory).memory
    val dstByteArray = (dst as ByteArrayLinearMemory).memory

    srcByteArray.copyInto(dstByteArray, dstOffset, srcOffset, srcOffset + copySize)
    return Ok(Unit)
}
