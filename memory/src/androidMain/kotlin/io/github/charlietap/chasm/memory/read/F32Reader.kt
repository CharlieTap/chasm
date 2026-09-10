@file:JvmName("F32ReaderAndroid")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.toFloatLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun F32Reader(
    memory: LinearMemory,
    address: Int,
): Float {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(address, Float.SIZE_BYTES, arrayMemory.byteSize)
    return arrayMemory.bytes.toFloatLittleEndian(address)
}
