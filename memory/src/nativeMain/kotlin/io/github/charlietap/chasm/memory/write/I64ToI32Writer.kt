package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import liblinmem.write_i64_to_i32

actual inline fun I64ToI32Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val nativeMemory = memory as NativeLinearMemory
    write_i64_to_i32(nativeMemory.pointer, address, value)
}
