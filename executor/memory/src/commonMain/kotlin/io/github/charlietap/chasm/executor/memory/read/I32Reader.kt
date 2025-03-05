package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias I32Reader = (LinearMemory, Int) -> Int

expect inline fun I32Reader(
    memory: LinearMemory,
    address: Int,
): Int
