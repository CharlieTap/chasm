package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.toDoubleLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun F64Reader(
    memory: LinearMemory,
    address: Int,
): Double {
    val array = (memory as ByteArrayLinearMemory).memory
    val value = array.sliceArray(
        address until (address + Double.SIZE_BYTES),
    ).toDoubleLittleEndian()
    return value
}
