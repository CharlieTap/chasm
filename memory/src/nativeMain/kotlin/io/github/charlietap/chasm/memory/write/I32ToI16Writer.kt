package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
actual inline fun I32ToI16Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
) {
    (memory as NativeMappedLinearMemory).storeI16Unchecked(address, value.toShort())
}
