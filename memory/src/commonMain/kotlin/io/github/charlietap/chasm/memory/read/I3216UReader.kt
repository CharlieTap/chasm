package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias I3216UReader = (LinearMemory, Int) -> Int

@InternalChasmApi
expect inline fun I3216UReader(
    memory: LinearMemory,
    address: Int,
): Int
