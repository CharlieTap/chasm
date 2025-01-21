package io.github.charlietap.chasm.executor.memory.write

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.write_i64_to_i16

actual inline fun I64ToI16Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val nativeMemory = memory as NativeLinearMemory
    write_i64_to_i16(nativeMemory.pointer, address, value)
}
