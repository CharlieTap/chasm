package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias F64Writer = (LinearMemory, Int, Double) -> Unit

@InternalChasmApi
expect inline fun F64Writer(
    memory: LinearMemory,
    address: Int,
    value: Double,
)
