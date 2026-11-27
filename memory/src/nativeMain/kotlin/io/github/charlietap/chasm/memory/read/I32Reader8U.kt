package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
actual inline fun I328UReader(
    memory: LinearMemory,
    address: Int,
): Int {
    return (memory as NativeMappedLinearMemory).loadI8Unchecked(address).toUByte().toInt()
}
