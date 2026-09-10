@file:JvmName("I3216UReaderAndroid")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.toShortLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I3216UReader(
    memory: LinearMemory,
    address: Int,
): Int {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(address, Short.SIZE_BYTES, arrayMemory.byteSize)
    return arrayMemory.bytes.toShortLittleEndian(address).toUShort().toInt()
}
