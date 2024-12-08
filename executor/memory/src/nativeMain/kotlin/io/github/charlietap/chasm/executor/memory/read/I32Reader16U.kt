package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.read_i32_from_u16

actual inline fun I3216UReader(
    memory: LinearMemory,
    address: Int,
): Result<Int, InvocationError> {
    val nativeMemory = memory as NativeLinearMemory
    return Ok(read_i32_from_u16(nativeMemory.pointer, address))
}
