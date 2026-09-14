package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I328SReader(
    memory: LinearMemory,
    address: Int,
): Int {
    return (memory as NativeMappedLinearMemory).loadI8Unchecked(address).toInt()
}
