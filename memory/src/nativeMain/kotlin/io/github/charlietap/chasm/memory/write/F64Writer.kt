package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
actual inline fun F64Writer(
    memory: LinearMemory,
    address: Int,
    value: Double,
) {
    (memory as NativeMappedLinearMemory).storeI64Unchecked(address, value.toRawBits())
}
