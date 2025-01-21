package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.read_i64_from_u32

actual inline fun I6432UReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val nativeMemory = memory as NativeLinearMemory
    return read_i64_from_u32(nativeMemory.pointer, address)
}
