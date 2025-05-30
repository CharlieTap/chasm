package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import liblinmem.write_i32_to_i16

actual inline fun I32ToI16Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
) {
    val nativeMemory = memory as NativeLinearMemory
    write_i32_to_i16(nativeMemory.pointer, address, value)
}
