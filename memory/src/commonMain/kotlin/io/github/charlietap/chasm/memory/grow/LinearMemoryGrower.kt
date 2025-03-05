package io.github.charlietap.chasm.memory.grow

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias LinearMemoryGrower = (LinearMemory, Int) -> LinearMemory

expect inline fun LinearMemoryGrower(
    memory: LinearMemory,
    pagesToAdd: Int,
): LinearMemory
