@file:JvmName("16432SReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.toIntLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I6432SReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray.sliceArray(address until address + Int.SIZE_BYTES).toIntLittleEndian()
    return value.toLong()
}
