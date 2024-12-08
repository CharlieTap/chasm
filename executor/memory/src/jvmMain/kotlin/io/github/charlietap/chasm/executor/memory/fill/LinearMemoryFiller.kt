@file:JvmName("LinearMemoryFillerJvm")

package io.github.charlietap.chasm.executor.memory.fill

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun LinearMemoryFiller(
    memory: LinearMemory,
    address: Int,
    bytesToFill: Int,
    fillValue: Byte,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> {
    val byteArray = (memory as ByteArrayLinearMemory)
    byteArray.memory.fill(fillValue, address, address + bytesToFill)
    return Ok(Unit)
}
