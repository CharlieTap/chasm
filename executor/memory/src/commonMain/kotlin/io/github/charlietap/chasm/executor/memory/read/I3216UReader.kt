package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias I3216UReader = (LinearMemory, Int) -> Int

expect inline fun I3216UReader(
    memory: LinearMemory,
    address: Int,
): Int
