package io.github.charlietap.chasm.memory.grow

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE

actual inline fun LinearMemoryGrower(
    memory: LinearMemory,
    pagesToAdd: Int,
): LinearMemory {
    val linearMemory = memory as ByteArrayLinearMemory
    val byteArray = linearMemory.memory
    val newSize = byteArray.size + (pagesToAdd * PAGE_SIZE)
    linearMemory.memory = byteArray.copyOf(newSize)
    return linearMemory
}
