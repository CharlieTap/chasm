package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun F64Writer(
    memory: LinearMemory,
    address: Int,
    value: Double,
) {
    (memory as NativeMappedLinearMemory).storeI64Unchecked(address, value.toRawBits())
}
