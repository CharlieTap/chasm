package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.toShortLittleEndian
import io.github.charlietap.chasm.memory.ext.toUShortLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I6416UReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val array = (memory as ByteArrayLinearMemory).memory
    return array.toUShortLittleEndian(address).toLong()
}
