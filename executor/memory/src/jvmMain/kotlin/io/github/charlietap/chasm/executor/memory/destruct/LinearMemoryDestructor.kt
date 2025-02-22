@file:JvmName("LinearMemoryDestructorJvm")

package io.github.charlietap.chasm.executor.memory.destruct

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual fun LinearMemoryDestructor(
    memory: LinearMemory,
) {
    (memory as? ByteArrayLinearMemory)?.memory = byteArrayOf()
}
