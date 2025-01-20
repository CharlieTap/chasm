package io.github.charlietap.chasm.executor.memory.copy

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias LinearMemoryCopier = (LinearMemory, LinearMemory, Int, Int, Int) -> Unit

expect inline fun LinearMemoryCopier(
    src: LinearMemory,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    copySize: Int,
)
