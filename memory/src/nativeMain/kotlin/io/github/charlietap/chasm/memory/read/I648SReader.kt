package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I648SReader(
    memory: LinearMemory,
    address: Int,
): Long {
    return (memory as NativeMappedLinearMemory).loadI8Unchecked(address).toLong()
}
