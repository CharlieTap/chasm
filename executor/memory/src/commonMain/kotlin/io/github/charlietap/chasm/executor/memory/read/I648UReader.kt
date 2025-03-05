package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias I648UReader = (LinearMemory, Int) -> Long

expect inline fun I648UReader(
    memory: LinearMemory,
    address: Int,
): Long
