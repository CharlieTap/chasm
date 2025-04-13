@file:JvmName("LinearMemoryDestructorJvm")

package io.github.charlietap.chasm.memory.destruct

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual fun LinearMemoryDestructor(
    memory: LinearMemory,
) {
    if (memory is ByteBufferLinearMemory) {
        memory.memory = ByteBuffer.allocateDirect(0).order(ByteOrder.LITTLE_ENDIAN)
    }
}
