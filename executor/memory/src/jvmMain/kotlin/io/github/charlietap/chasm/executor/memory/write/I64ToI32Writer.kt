@file:JvmName("I64ToI32WriterJvm")

package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.copyInto
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun I64ToI32Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Result<Unit, InvocationError> {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    value.copyInto(byteArray, address, 4)
    return Ok(Unit)
}
