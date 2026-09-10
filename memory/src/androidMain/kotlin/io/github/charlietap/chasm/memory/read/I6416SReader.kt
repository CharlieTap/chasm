@file:JvmName("I6416SReaderAndroid")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.toShortLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I6416SReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(address, Short.SIZE_BYTES, arrayMemory.byteSize)
    return arrayMemory.bytes.toShortLittleEndian(address).toLong()
}
