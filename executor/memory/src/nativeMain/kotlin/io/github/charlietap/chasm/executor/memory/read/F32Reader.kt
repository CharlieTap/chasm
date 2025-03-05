package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import liblinmem.read_f32

actual inline fun F32Reader(
    memory: LinearMemory,
    address: Int,
): Float {
    val nativeMemory = memory as NativeLinearMemory
    return read_f32(nativeMemory.pointer, address)
}
