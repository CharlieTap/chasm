package io.github.charlietap.chasm.memory.init

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias LinearMemoryLoader = (LinearMemory, Int, Int, Int, (ByteArray, Int) -> Unit) -> Unit

@InternalChasmApi
expect inline fun LinearMemoryLoader(
    dst: LinearMemory,
    dstOffset: Int,
    bytesToLoad: Int,
    dstUpperBound: Int,
    load: (ByteArray, Int) -> Unit,
)
