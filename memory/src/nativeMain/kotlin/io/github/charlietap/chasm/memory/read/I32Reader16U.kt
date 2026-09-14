package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.NativeMappedLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I3216UReader(
    memory: LinearMemory,
    address: Int,
): Int {
    return (memory as NativeMappedLinearMemory).loadI16Unchecked(address).toUShort().toInt()
}
