@file:JvmName("13216UReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.toUShortLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I3216UReader(
    memory: LinearMemory,
    address: Int,
): Int {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray.sliceArray(address until address + Short.SIZE_BYTES).toUShortLittleEndian()
    return value.toInt()
}
