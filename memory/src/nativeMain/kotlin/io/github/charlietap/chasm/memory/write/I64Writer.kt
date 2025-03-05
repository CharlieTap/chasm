package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import liblinmem.write_i64

actual inline fun I64Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val nativeMemory = memory as NativeLinearMemory
    write_i64(nativeMemory.pointer, address, value)
}
