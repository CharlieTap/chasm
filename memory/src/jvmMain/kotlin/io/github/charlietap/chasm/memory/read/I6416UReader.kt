@file:JvmName("16416UReaderJvm")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.toUShortLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I6416UReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray.sliceArray(address until address + Short.SIZE_BYTES).toUShortLittleEndian()
    return value.toLong()
}
