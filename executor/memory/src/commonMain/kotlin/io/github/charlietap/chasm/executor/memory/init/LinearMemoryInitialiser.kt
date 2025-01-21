package io.github.charlietap.chasm.executor.memory.init

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias LinearMemoryInitialiser = (UByteArray, LinearMemory, Int, Int, Int) -> Unit

expect inline fun LinearMemoryInitialiser(
    src: UByteArray,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    bytesToInit: Int,
)
