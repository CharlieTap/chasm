package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
) {
    (memory as NativeMappedLinearMemory).storeI32Unchecked(address, value)
}
