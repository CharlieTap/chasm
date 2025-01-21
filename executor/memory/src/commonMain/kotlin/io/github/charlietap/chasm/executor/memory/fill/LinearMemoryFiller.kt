package io.github.charlietap.chasm.executor.memory.fill

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias LinearMemoryFiller = (LinearMemory, Int, Int, Byte) -> Unit

expect inline fun LinearMemoryFiller(
    memory: LinearMemory,
    address: Int,
    bytesToFill: Int,
    fillValue: Byte,
)
