package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias I648SReader = (LinearMemory, Int) -> Long

expect inline fun I648SReader(
    memory: LinearMemory,
    address: Int,
): Long
