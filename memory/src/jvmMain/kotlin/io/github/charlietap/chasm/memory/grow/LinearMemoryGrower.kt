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
    val linearMemory = memory as ByteBufferLinearMemory
    val buffer = linearMemory.memory
    val currentSize = buffer.limit()
    val newSize = currentSize + (pagesToAdd * PAGE_SIZE)

    if (newSize <= buffer.capacity()) {
        buffer.limit(newSize)
        return linearMemory
    }

    val doubledCapacity = minOf(buffer.capacity().toLong() * 2, Int.MAX_VALUE.toLong()).toInt()
    val reservedCapacity = minOf(newSize.toLong() + (newSize / 2), Int.MAX_VALUE.toLong()).toInt()
    val newCapacity = maxOf(doubledCapacity, reservedCapacity)
    val newBuffer = try {
        ByteBuffer.allocateDirect(newCapacity)
    } catch (error: OutOfMemoryError) {
        if (newCapacity == newSize) throw error
        ByteBuffer.allocateDirect(newSize)
    }.order(ByteOrder.LITTLE_ENDIAN)

    buffer.duplicate().apply {
        position(0)
        limit(currentSize)
        newBuffer.put(this)
    }
    newBuffer.position(0)
    newBuffer.limit(newSize)
    linearMemory.memory = newBuffer
    return linearMemory
}
