package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias F32Reader = (LinearMemory, Int) -> Float

@InternalChasmApi
expect inline fun F32Reader(
    memory: LinearMemory,
    address: Int,
): Float
