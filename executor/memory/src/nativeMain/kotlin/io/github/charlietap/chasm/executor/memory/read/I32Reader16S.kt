package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.read_i32_from_i16

actual inline fun I3216SReader(
    memory: LinearMemory,
    address: Int,
): Int {
    val nativeMemory = memory as NativeLinearMemory
    return read_i32_from_i16(nativeMemory.pointer, address)
}
