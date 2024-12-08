@file:JvmName("13216UReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.toUShortLittleEndian
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun I3216UReader(
    memory: LinearMemory,
    address: Int,
): Result<Int, InvocationError> {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray.sliceArray(address until address + Short.SIZE_BYTES).toUShortLittleEndian()
    return Ok(value.toInt())
}
