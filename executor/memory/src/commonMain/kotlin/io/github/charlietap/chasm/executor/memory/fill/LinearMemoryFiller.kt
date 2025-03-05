package io.github.charlietap.chasm.executor.memory.fill

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias LinearMemoryFiller = (LinearMemory, Int, Int, Byte, Int) -> Unit

expect inline fun LinearMemoryFiller(
    memory: LinearMemory,
    address: Int,
    bytesToFill: Int,
    fillValue: Byte,
    upperBound: Int,
)
