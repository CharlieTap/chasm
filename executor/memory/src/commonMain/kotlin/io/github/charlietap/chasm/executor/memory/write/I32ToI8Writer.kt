package io.github.charlietap.chasm.executor.memory.write

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias I32ToI8Writer = (LinearMemory, Int, Int) -> Unit

expect inline fun I32ToI8Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
)
