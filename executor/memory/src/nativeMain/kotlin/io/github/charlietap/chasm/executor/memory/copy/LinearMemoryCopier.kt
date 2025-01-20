package io.github.charlietap.chasm.executor.memory.copy

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.copy

actual inline fun LinearMemoryCopier(
    src: LinearMemory,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    copySize: Int,
) {

    val srcMemory = (src as NativeLinearMemory)
    val dstMemory = (dst as NativeLinearMemory)

    copy(srcMemory.pointer, dstMemory.pointer, srcOffset, dstOffset, copySize)
}
