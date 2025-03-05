package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias F64Writer = (LinearMemory, Int, Double) -> Unit

expect inline fun F64Writer(
    memory: LinearMemory,
    address: Int,
    value: Double,
)
