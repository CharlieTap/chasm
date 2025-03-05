package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import liblinmem.read_i64_from_i8

actual inline fun I648SReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val nativeMemory = memory as NativeLinearMemory
    return read_i64_from_i8(nativeMemory.pointer, address)
}
