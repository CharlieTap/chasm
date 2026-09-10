@file:JvmName("I6432SReaderAndroid")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.toIntLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I6432SReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(address, Int.SIZE_BYTES, arrayMemory.byteSize)
    return arrayMemory.bytes.toIntLittleEndian(address).toLong()
}
