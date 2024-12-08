@file:JvmName("I648SReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun I648SReader(
    memory: LinearMemory,
    address: Int,
): Result<Long, InvocationError> {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray[address].toLong()
    return Ok(value)
}
