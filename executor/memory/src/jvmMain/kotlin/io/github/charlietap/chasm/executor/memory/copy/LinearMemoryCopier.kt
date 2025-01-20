@file:JvmName("LinearMemoryCopierJvm")

package io.github.charlietap.chasm.executor.memory.copy

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun LinearMemoryCopier(
    src: LinearMemory,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    copySize: Int,
) {

    val srcByteArray = (src as ByteArrayLinearMemory).memory
    val dstByteArray = (dst as ByteArrayLinearMemory).memory

    srcByteArray.copyInto(dstByteArray, dstOffset, srcOffset, srcOffset + copySize)
}
