@file:JvmName("I328UReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun I328UReader(
    memory: LinearMemory,
    address: Int,
): Result<Int, InvocationError> {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray[address].toUByte().toInt()
    return Ok(value)
}
