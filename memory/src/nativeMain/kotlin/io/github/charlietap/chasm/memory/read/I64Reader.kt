package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
actual inline fun I64Reader(
    memory: LinearMemory,
    address: Int,
): Long {
    return (memory as NativeMappedLinearMemory).loadI64Unchecked(address)
}
