@file:JvmName("I64ReaderAndroid")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.toLongLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I64Reader(
    memory: LinearMemory,
    address: Int,
): Long {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(address, Long.SIZE_BYTES, arrayMemory.byteSize)
    return arrayMemory.bytes.toLongLittleEndian(address)
}
