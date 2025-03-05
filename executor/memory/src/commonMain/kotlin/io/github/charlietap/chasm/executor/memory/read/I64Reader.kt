package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias I64Reader = (LinearMemory, Int) -> Long

expect inline fun I64Reader(
    memory: LinearMemory,
    address: Int,
): Long
