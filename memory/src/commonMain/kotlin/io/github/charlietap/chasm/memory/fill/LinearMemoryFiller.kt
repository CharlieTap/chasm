package io.github.charlietap.chasm.memory.fill

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias LinearMemoryFiller = (LinearMemory, Int, Int, Byte, Int) -> Unit

@InternalChasmApi
expect fun LinearMemoryFiller(
    memory: LinearMemory,
    address: Int,
    bytesToFill: Int,
    fillValue: Byte,
    upperBound: Int,
)
