package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias I328SReader = (LinearMemory, Int) -> Int

expect inline fun I328SReader(
    memory: LinearMemory,
    address: Int,
): Int
