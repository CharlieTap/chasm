package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import liblinmem.write_i32

actual inline fun I32Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
) {
    val nativeMemory = memory as NativeLinearMemory
    write_i32(nativeMemory.pointer, address, value)
}
