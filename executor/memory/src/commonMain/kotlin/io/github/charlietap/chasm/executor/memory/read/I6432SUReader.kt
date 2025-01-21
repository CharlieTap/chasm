package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias I6432UReader = (LinearMemory, Int) -> Long

expect inline fun I6432UReader(
    memory: LinearMemory,
    address: Int,
): Long
