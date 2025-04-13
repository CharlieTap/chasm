@file:JvmName("I32ToI16WriterJvm")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32ToI16Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    buffer.putShort(address, value.toShort())
}
