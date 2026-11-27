@file:JvmName("LinearMemoryDestructorJvm")

package io.github.charlietap.chasm.memory.destruct

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
actual fun LinearMemoryDestructor(
    memory: LinearMemory,
) {
    if (memory is ByteBufferLinearMemory) {
        memory.release()
    }
}
