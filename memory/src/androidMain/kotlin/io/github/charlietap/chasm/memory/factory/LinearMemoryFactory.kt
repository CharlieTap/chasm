@file:JvmName("LinearMemoryFactoryAndroid")

package io.github.charlietap.chasm.memory.factory

import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.MAX_ANDROID_MEMORY_PAGES
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@Suppress("UNUSED_PARAMETER")
actual fun LinearMemoryFactory(
    pages: LinearMemory.Pages,
    maximumPages: LinearMemory.Pages?,
    config: LinearMemoryConfig,
): LinearMemory {
    val maximum = maximumPages?.amount?.let { amount ->
        minOf(amount, MAX_ANDROID_MEMORY_PAGES.toUInt()).toInt()
    }
    return if (maximum == null) ByteArrayLinearMemory(pages) else ByteArrayLinearMemory(pages, maximum)
}
