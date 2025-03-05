package io.github.charlietap.chasm.executor.memory.write

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias I64Writer = (LinearMemory, Int, Long) -> Unit

expect inline fun I64Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
)
