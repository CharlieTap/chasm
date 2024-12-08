package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.write_i64_to_i16

actual inline fun I64ToI16Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Result<Unit, InvocationError> {
    val nativeMemory = memory as NativeLinearMemory
    write_i64_to_i16(nativeMemory.pointer, address, value)
    return Ok(Unit)
}
