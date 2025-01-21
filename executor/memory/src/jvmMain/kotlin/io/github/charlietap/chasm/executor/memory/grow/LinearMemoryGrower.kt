@file:JvmName("LinearMemoryGrowerJvm")

package io.github.charlietap.chasm.executor.memory.grow

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory.Companion.PAGE_SIZE

actual inline fun LinearMemoryGrower(
    memory: LinearMemory,
    pagesToAdd: Int,
): LinearMemory {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val newSize = byteArray.size + (pagesToAdd * PAGE_SIZE)
    val newByteArray = byteArray.copyOf(newSize)
    return ByteArrayLinearMemory(newByteArray)
}
