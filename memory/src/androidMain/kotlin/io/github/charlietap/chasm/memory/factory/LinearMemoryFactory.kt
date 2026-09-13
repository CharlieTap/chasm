@file:JvmName("LinearMemoryFactoryAndroid")

package io.github.charlietap.chasm.memory.factory

import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@Suppress("UNUSED_PARAMETER")
actual fun LinearMemoryFactory(
    pages: LinearMemory.Pages,
    maximumPages: LinearMemory.Pages?,
    config: LinearMemoryConfig,
): LinearMemory {
    return ByteArrayLinearMemory(pages)
}
