package io.github.charlietap.chasm.executor.memory.grow

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias LinearMemoryGrower = (LinearMemory, Int) -> LinearMemory

expect inline fun LinearMemoryGrower(
    memory: LinearMemory,
    pagesToAdd: Int,
): LinearMemory
