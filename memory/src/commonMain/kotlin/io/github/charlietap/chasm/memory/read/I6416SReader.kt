package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias I6416SReader = (LinearMemory, Int) -> Long

expect inline fun I6416SReader(
    memory: LinearMemory,
    address: Int,
): Long
