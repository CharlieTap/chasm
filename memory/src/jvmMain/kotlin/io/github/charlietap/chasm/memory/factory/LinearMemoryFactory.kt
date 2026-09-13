@file:JvmName("LinearMemoryFactoryJvm")

package io.github.charlietap.chasm.memory.factory

import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual fun LinearMemoryFactory(
    pages: LinearMemory.Pages,
    maximumPages: LinearMemory.Pages?,
    config: LinearMemoryConfig,
): LinearMemory {
    return ByteBufferLinearMemory(pages, maximumPages, config)
}
