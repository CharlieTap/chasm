@file:JvmName("I32ReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.toIntLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32Reader(
    memory: LinearMemory,
    address: Int,
): Int {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray
        .sliceArray(
            address until (address + Int.SIZE_BYTES),
        ).toIntLittleEndian()
    return value
}
