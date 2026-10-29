package io.github.charlietap.chasm.memory.init

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias LinearMemoryLoader = (LinearMemory, Int, Int, Int, (ByteArray, Int) -> Unit) -> Unit

expect inline fun LinearMemoryLoader(
    dst: LinearMemory,
    dstOffset: Int,
    bytesToLoad: Int,
    dstUpperBound: Int,
    load: (ByteArray, Int) -> Unit,
)
