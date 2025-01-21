@file:JvmName("16432UReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.toUIntLittleEndian
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun I6432UReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray.sliceArray(address until address + Int.SIZE_BYTES).toUIntLittleEndian()
    return value.toLong()
}
