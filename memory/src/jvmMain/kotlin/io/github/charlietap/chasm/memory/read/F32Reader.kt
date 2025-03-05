@file:JvmName("F32ReaderJvm")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.toFloatLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun F32Reader(
    memory: LinearMemory,
    address: Int,
): Float {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray
        .sliceArray(
            address until (address + Float.SIZE_BYTES),
        ).toFloatLittleEndian()
    return value
}
