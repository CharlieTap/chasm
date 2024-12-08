@file:JvmName("I64ReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.toLongLittleEndian
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun I64Reader(
    memory: LinearMemory,
    address: Int,
): Result<Long, InvocationError> {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray.sliceArray(
        address until (address + Long.SIZE_BYTES),
    ).toLongLittleEndian()
    return Ok(value)
}
