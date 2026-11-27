package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias I648UReader = (LinearMemory, Int) -> Long

@InternalChasmApi
expect inline fun I648UReader(
    memory: LinearMemory,
    address: Int,
): Long
