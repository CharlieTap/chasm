package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias I328UReader = (LinearMemory, Int) -> Int

@InternalChasmApi
expect inline fun I328UReader(
    memory: LinearMemory,
    address: Int,
): Int
