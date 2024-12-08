package io.github.charlietap.chasm.executor.memory.fill

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.fill

actual inline fun LinearMemoryFiller(
    memory: LinearMemory,
    address: Int,
    bytesToFill: Int,
    fillValue: Byte,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> {
    val memory = (memory as NativeLinearMemory)
    fill(memory.pointer, address, bytesToFill, fillValue.toUByte())
    return Ok(Unit)
}
