package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32Reader(
    memory: LinearMemory,
    address: Int,
): Int {
    return (memory as NativeMappedLinearMemory).loadI32Unchecked(address)
}
