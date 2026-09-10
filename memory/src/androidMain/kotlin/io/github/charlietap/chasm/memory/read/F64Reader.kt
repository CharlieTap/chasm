@file:JvmName("F64ReaderAndroid")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.toDoubleLittleEndian
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun F64Reader(
    memory: LinearMemory,
    address: Int,
): Double {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(address, Double.SIZE_BYTES, arrayMemory.byteSize)
    return arrayMemory.bytes.toDoubleLittleEndian(address)
}
