package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.write_f64

actual inline fun F64Writer(
    memory: LinearMemory,
    address: Int,
    value: Double,
): Result<Unit, InvocationError> {
    val nativeMemory = memory as NativeLinearMemory
    write_f64(nativeMemory.pointer, address, value)
    return Ok(Unit)
}
