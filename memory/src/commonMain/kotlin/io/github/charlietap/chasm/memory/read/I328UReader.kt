package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias I328UReader = (LinearMemory, Int) -> Int

expect inline fun I328UReader(
    memory: LinearMemory,
    address: Int,
): Int
