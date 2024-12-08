@file:JvmName("LinearMemoryInitialiserJvm")

package io.github.charlietap.chasm.executor.memory.init

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun LinearMemoryInitialiser(
    src: UByteArray,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    bytesToInit: Int,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = binding {
    val byteArray = (dst as ByteArrayLinearMemory).memory
    src.asByteArray().copyInto(byteArray, dstOffset, srcOffset, srcOffset + bytesToInit)
}
