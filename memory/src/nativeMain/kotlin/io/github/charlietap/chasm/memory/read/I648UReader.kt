package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import liblinmem.read_i64_from_u8

actual inline fun I648UReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val nativeMemory = memory as NativeLinearMemory
    return read_i64_from_u8(nativeMemory.pointer, address)
}
