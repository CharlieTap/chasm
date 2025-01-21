package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias I6416SReader = (LinearMemory, Int) -> Long

expect inline fun I6416SReader(
    memory: LinearMemory,
    address: Int,
): Long
