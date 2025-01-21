package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.read_i32_from_i8

actual inline fun I328SReader(
    memory: LinearMemory,
    address: Int,
): Int {
    val nativeMemory = memory as NativeLinearMemory
    return read_i32_from_i8(nativeMemory.pointer, address)
}
