package io.github.charlietap.chasm.memory.init

import io.github.charlietap.chasm.runtime.memory.LinearMemory

typealias LinearMemoryInitialiser = (UByteArray, LinearMemory, Int, Int, Int, Int, Int) -> Unit

expect inline fun LinearMemoryInitialiser(
    src: UByteArray,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    bytesToInit: Int,
    srcUpperBound: Int,
    dstUpperBound: Int,
)
