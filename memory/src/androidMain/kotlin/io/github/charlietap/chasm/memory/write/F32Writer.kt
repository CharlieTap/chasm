@file:JvmName("F32WriterAndroid")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.memory.ext.copyInto
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun F32Writer(
    memory: LinearMemory,
    address: Int,
    value: Float,
) {
    val arrayMemory = memory as ByteArrayLinearMemory
    BoundsChecker(address, Float.SIZE_BYTES, arrayMemory.byteSize)
    value.copyInto(arrayMemory.bytes, address)
}
