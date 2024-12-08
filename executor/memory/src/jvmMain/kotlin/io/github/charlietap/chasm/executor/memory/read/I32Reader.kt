@file:JvmName("I32ReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.toIntLittleEndian
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun I32Reader(
    memory: LinearMemory,
    address: Int,
): Result<Int, InvocationError> {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray.sliceArray(
        address until (address + Int.SIZE_BYTES),
    ).toIntLittleEndian()
    return Ok(value)
}
