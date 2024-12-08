@file:JvmName("LinearMemoryGrowerJvm")

package io.github.charlietap.chasm.executor.memory.grow

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory.Companion.PAGE_SIZE

actual inline fun LinearMemoryGrower(
    memory: LinearMemory,
    pagesToAdd: Int,
): Result<LinearMemory, InvocationError> = binding {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val newSize = byteArray.size + (pagesToAdd * PAGE_SIZE)
    val newByteArray = byteArray.copyOf(newSize)
    ByteArrayLinearMemory(newByteArray)
}
