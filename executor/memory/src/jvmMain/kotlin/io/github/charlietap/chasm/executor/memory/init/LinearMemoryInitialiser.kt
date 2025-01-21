@file:JvmName("LinearMemoryInitialiserJvm")

package io.github.charlietap.chasm.executor.memory.init

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun LinearMemoryInitialiser(
    src: UByteArray,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    bytesToInit: Int,
) {
    val byteArray = (dst as ByteArrayLinearMemory).memory
    src.asByteArray().copyInto(byteArray, dstOffset, srcOffset, srcOffset + bytesToInit)
}
