package io.github.charlietap.chasm.executor.instantiator.allocation.memory

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

internal typealias LinearMemoryFactory = (min: Int, max: Int?) -> LinearMemory
