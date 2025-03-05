@file:JvmName("LinearMemoryDestructorJvm")

package io.github.charlietap.chasm.memory.destruct

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual fun LinearMemoryDestructor(
    memory: LinearMemory,
) {
    (memory as? ByteArrayLinearMemory)?.memory = byteArrayOf()
}
