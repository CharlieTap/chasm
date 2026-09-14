package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I6416SReader(
    memory: LinearMemory,
    address: Int,
): Long {
    return (memory as NativeMappedLinearMemory).loadI16Unchecked(address).toLong()
}
