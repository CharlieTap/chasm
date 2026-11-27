package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias I32Reader = (LinearMemory, Int) -> Int

@InternalChasmApi
expect inline fun I32Reader(
    memory: LinearMemory,
    address: Int,
): Int
