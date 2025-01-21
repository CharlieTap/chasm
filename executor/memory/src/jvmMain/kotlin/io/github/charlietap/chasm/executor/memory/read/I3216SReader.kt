@file:JvmName("13216SReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.toShortLittleEndian
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun I3216SReader(
    memory: LinearMemory,
    address: Int,
): Int {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray.sliceArray(address until address + Short.SIZE_BYTES).toShortLittleEndian()
    return value.toInt()
}
