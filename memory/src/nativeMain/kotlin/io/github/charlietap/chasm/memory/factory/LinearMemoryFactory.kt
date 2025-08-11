package io.github.charlietap.chasm.memory.factory

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual fun LinearMemoryFactory(pages: LinearMemory.Pages): LinearMemory {
    return ByteArrayLinearMemory(pages)
}
