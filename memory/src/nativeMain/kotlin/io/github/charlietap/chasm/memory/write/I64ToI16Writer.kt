package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.copyInto
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I64ToI16Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val array = (memory as ByteArrayLinearMemory).memory
    value.copyInto(array, address, size = 2)
}
