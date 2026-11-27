package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
actual inline fun F64Reader(
    memory: LinearMemory,
    address: Int,
): Double {
    return Double.fromBits((memory as NativeMappedLinearMemory).loadI64Unchecked(address))
}
