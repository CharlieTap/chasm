package io.github.charlietap.chasm.executor.memory.write

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias I32ToI16Writer = (LinearMemory, Int, Int) -> Unit

expect inline fun I32ToI16Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
)
