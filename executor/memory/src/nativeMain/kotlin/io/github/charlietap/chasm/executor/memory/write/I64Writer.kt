package io.github.charlietap.chasm.executor.memory.write

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.write_i64

actual inline fun I64Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val nativeMemory = memory as NativeLinearMemory
    write_i64(nativeMemory.pointer, address, value)
}
