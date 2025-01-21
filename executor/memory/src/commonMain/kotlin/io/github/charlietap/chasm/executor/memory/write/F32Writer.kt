package io.github.charlietap.chasm.executor.memory.write

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias F32Writer = (LinearMemory, Int, Float) -> Unit

expect inline fun F32Writer(
    memory: LinearMemory,
    address: Int,
    value: Float,
)
