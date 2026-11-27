@file:JvmName("I328UReaderAndroid")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

@InternalChasmApi
actual inline fun I328UReader(
    memory: LinearMemory,
    address: Int,
): Int {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(address, Byte.SIZE_BYTES, arrayMemory.byteSize)
    return arrayMemory.bytes[address].toUByte().toInt()
}
