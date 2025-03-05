package io.github.charlietap.chasm.executor.memory.write

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import liblinmem.write_f64

actual inline fun F64Writer(
    memory: LinearMemory,
    address: Int,
    value: Double,
) {
    val nativeMemory = memory as NativeLinearMemory
    write_f64(nativeMemory.pointer, address, value)
}
