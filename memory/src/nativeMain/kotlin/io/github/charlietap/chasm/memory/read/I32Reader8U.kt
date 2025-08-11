package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I328UReader(
    memory: LinearMemory,
    address: Int,
): Int {
    val array = (memory as ByteArrayLinearMemory).memory
    return array[address].toUByte().toInt()
}
