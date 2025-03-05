package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias I64ToI16Writer = (LinearMemory, Int, Long) -> Unit

expect inline fun I64ToI16Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
)
