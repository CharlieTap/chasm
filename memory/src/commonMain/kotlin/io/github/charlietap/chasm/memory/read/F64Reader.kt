package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias F64Reader = (LinearMemory, Int) -> Double

@InternalChasmApi
expect inline fun F64Reader(
    memory: LinearMemory,
    address: Int,
): Double
