@file:JvmName("I64WriterAndroid")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.copyInto
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I64Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(address, Long.SIZE_BYTES, arrayMemory.byteSize)
    value.copyInto(arrayMemory.bytes, address)
}
