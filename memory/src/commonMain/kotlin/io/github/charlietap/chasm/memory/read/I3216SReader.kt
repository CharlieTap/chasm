package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias I3216SReader = (LinearMemory, Int) -> Int

@InternalChasmApi
expect inline fun I3216SReader(
    memory: LinearMemory,
    address: Int,
): Int
