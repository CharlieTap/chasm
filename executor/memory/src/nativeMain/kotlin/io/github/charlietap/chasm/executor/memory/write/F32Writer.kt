package io.github.charlietap.chasm.executor.memory.write

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.write_f32

actual inline fun F32Writer(
    memory: LinearMemory,
    address: Int,
    value: Float,
) {
    val nativeMemory = memory as NativeLinearMemory
    write_f32(nativeMemory.pointer, address, value)
}
