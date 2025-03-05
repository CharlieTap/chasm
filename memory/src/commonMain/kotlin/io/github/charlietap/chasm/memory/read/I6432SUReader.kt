package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias I6432UReader = (LinearMemory, Int) -> Long

expect inline fun I6432UReader(
    memory: LinearMemory,
    address: Int,
): Long
