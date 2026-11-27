package io.github.charlietap.chasm.memory.copy

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias LinearMemoryCopier = (LinearMemory, LinearMemory, Int, Int, Int, Int, Int) -> Unit

@InternalChasmApi
expect inline fun LinearMemoryCopier(
    src: LinearMemory,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    copySize: Int,
    srcUpperBound: Int,
    dstUpperBound: Int,
)
