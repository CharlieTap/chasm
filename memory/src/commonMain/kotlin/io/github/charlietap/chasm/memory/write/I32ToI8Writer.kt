package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias I32ToI8Writer = (LinearMemory, Int, Int) -> Unit

expect inline fun I32ToI8Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
)
