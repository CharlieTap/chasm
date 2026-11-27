package io.github.charlietap.chasm.memory.factory

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias LinearMemoryFactory = (
    pages: LinearMemory.Pages,
    maximumPages: LinearMemory.Pages?,
) -> LinearMemory

@InternalChasmApi
expect fun LinearMemoryFactory(
    pages: LinearMemory.Pages,
    maximumPages: LinearMemory.Pages? = null,
): LinearMemory
