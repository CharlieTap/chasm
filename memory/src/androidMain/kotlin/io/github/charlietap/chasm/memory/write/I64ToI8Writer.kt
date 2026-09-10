@file:JvmName("I64ToI8WriterAndroid")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I64ToI8Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(address, Byte.SIZE_BYTES, arrayMemory.byteSize)
    arrayMemory.bytes[address] = value.toByte()
}
