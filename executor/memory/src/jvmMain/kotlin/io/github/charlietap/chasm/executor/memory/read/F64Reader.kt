@file:JvmName("F64ReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.toDoubleLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun F64Reader(
    memory: LinearMemory,
    address: Int,
): Double {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray
        .sliceArray(
            address until (address + Double.SIZE_BYTES),
        ).toDoubleLittleEndian()
    return value
}
