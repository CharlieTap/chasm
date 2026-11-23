package io.github.charlietap.chasm.memory.factory

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual fun LinearMemoryFactory(
    pages: LinearMemory.Pages,
    maximumPages: LinearMemory.Pages?,
): LinearMemory {
    return NativeMappedLinearMemory.create(pages, maximumPages)
}
