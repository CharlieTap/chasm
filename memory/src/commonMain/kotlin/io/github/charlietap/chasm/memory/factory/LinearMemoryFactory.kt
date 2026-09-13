package io.github.charlietap.chasm.memory.factory

import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias LinearMemoryFactory = (
    pages: LinearMemory.Pages,
    maximumPages: LinearMemory.Pages?,
    config: LinearMemoryConfig,
) -> LinearMemory

expect fun LinearMemoryFactory(
    pages: LinearMemory.Pages,
    maximumPages: LinearMemory.Pages? = null,
    config: LinearMemoryConfig = LinearMemoryConfig(),
): LinearMemory
