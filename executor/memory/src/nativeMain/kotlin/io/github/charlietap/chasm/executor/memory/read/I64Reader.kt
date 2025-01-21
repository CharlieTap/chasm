package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.read_i64

actual inline fun I64Reader(
    memory: LinearMemory,
    address: Int,
): Long {
    val nativeMemory = memory as NativeLinearMemory
    return read_i64(nativeMemory.pointer, address)
}
