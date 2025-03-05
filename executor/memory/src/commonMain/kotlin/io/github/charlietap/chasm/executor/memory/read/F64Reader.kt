package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias F64Reader = (LinearMemory, Int) -> Double

expect inline fun F64Reader(
    memory: LinearMemory,
    address: Int,
): Double
