@file:JvmName("LinearMemoryFactoryJvm")

package io.github.charlietap.chasm.executor.memory.factory

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual fun LinearMemoryFactory(pages: LinearMemory.Pages): LinearMemory {
    return ByteArrayLinearMemory(pages)
}
