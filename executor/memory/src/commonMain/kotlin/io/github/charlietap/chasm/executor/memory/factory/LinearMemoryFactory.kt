package io.github.charlietap.chasm.executor.memory.factory

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias LinearMemoryFactory = (min: Int, max: Int?) -> LinearMemory
