@file:JvmName("I648SReaderAndroid")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I648SReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(address, Byte.SIZE_BYTES, arrayMemory.byteSize)
    return arrayMemory.bytes[address].toLong()
}
