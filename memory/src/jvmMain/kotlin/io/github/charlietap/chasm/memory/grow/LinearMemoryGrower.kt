@file:JvmName("LinearMemoryGrowerJvm")

package io.github.charlietap.chasm.memory.grow

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual inline fun LinearMemoryGrower(
    memory: LinearMemory,
    pagesToAdd: Int,
): LinearMemory {
    val srcBuffer = (memory as ByteBufferLinearMemory).memory
    val currentSize = srcBuffer.capacity()
    val newSize = currentSize + (pagesToAdd * PAGE_SIZE)

    val newBuffer = ByteBuffer.allocateDirect(newSize).order(ByteOrder.LITTLE_ENDIAN)

    srcBuffer.duplicate().apply {
        position(0)
        limit(currentSize)
        newBuffer.put(this)
    }

    return ByteBufferLinearMemory(newBuffer)
}
