package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias I6416UReader = (LinearMemory, Int) -> Long

expect inline fun I6416UReader(
    memory: LinearMemory,
    address: Int,
): Long
