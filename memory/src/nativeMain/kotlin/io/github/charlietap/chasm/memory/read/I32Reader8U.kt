package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import liblinmem.read_i32_from_u8

actual inline fun I328UReader(
    memory: LinearMemory,
    address: Int,
): Int {
    val nativeMemory = memory as NativeLinearMemory
    return read_i32_from_u8(nativeMemory.pointer, address)
}
