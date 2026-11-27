package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias I32Writer = (LinearMemory, Int, Int) -> Unit

@InternalChasmApi
expect inline fun I32Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
)
