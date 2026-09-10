@file:JvmName("I32ToI16WriterAndroid")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32ToI16Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
) {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(address, Short.SIZE_BYTES, arrayMemory.byteSize)
    val bytes = arrayMemory.bytes
    bytes[address] = value.toByte()
    bytes[address + 1] = (value ushr 8).toByte()
}
