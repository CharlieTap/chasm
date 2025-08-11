package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.toIntLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I6432SReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val array = (memory as ByteArrayLinearMemory).memory
    return array.toIntLittleEndian(address).toLong()
}
