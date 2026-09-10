@file:JvmName("I32ReaderAndroid")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.toIntLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32Reader(
    memory: LinearMemory,
    address: Int,
): Int {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(address, Int.SIZE_BYTES, arrayMemory.byteSize)
    return arrayMemory.bytes.toIntLittleEndian(address)
}
