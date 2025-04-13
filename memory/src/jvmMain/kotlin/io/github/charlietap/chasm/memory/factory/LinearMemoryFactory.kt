@file:JvmName("LinearMemoryFactoryJvm")

package io.github.charlietap.chasm.memory.factory

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual fun LinearMemoryFactory(pages: LinearMemory.Pages): LinearMemory {
    return ByteBufferLinearMemory(pages)
}
