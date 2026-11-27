package io.github.charlietap.chasm.memory.init

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
typealias LinearMemoryInitialiser = (UByteArray, LinearMemory, Int, Int, Int, Int, Int) -> Unit

@InternalChasmApi
expect inline fun LinearMemoryInitialiser(
    src: UByteArray,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    bytesToInit: Int,
    srcUpperBound: Int,
    dstUpperBound: Int,
)
