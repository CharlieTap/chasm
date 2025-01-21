@file:JvmName("I64ReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.toLongLittleEndian
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun I64Reader(
    memory: LinearMemory,
    address: Int,
): Long {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray
        .sliceArray(
            address until (address + Long.SIZE_BYTES),
        ).toLongLittleEndian()
    return value
}
