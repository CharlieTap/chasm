package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.read_f64

actual inline fun F64Reader(
    memory: LinearMemory,
    address: Int,
): Double {
    val nativeMemory = memory as NativeLinearMemory
    return read_f64(nativeMemory.pointer, address)
}
