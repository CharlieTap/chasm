package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias F32Writer = (LinearMemory, Int, Float) -> Unit

@InternalChasmApi
expect inline fun F32Writer(
    memory: LinearMemory,
    address: Int,
    value: Float,
)
