@file:JvmName("F32WriterJvm")

package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.copyInto
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun F32Writer(
    memory: LinearMemory,
    address: Int,
    value: Float,
): Result<Unit, InvocationError> {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    value.copyInto(byteArray, address)
    return Ok(Unit)
}
