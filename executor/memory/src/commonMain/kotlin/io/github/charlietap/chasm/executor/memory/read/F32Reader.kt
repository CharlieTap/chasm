package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias F32Reader = (LinearMemory, Int) -> Float

expect inline fun F32Reader(
    memory: LinearMemory,
    address: Int,
): Float
