package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun F32Reader(
    memory: LinearMemory,
    address: Int,
): Float {
    return Float.fromBits((memory as NativeMappedLinearMemory).loadI32Unchecked(address))
}
