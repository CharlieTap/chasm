package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun F32Writer(
    memory: LinearMemory,
    address: Int,
    value: Float,
) {
    (memory as NativeMappedLinearMemory).storeI32Unchecked(address, value.toRawBits())
}
