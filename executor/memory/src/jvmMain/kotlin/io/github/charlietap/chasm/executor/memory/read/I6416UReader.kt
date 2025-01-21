@file:JvmName("16416UReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.toUShortLittleEndian
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun I6416UReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray.sliceArray(address until address + Short.SIZE_BYTES).toUShortLittleEndian()
    return value.toLong()
}
