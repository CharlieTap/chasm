@file:JvmName("LinearMemoryFactoryJvm")

package io.github.charlietap.chasm.memory.factory

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
actual fun LinearMemoryFactory(
    pages: LinearMemory.Pages,
    maximumPages: LinearMemory.Pages?,
): LinearMemory {
    return ByteBufferLinearMemory(pages, maximumPages)
}
