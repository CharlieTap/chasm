package io.github.charlietap.chasm.executor.memory.factory

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias LinearMemoryFactory = (pages: LinearMemory.Pages) -> LinearMemory

expect fun LinearMemoryFactory(
    pages: LinearMemory.Pages,
): LinearMemory
