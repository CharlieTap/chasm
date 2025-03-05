package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias I3216SReader = (LinearMemory, Int) -> Int

expect inline fun I3216SReader(
    memory: LinearMemory,
    address: Int,
): Int
