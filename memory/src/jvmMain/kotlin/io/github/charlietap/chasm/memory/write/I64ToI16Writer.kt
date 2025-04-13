@file:JvmName("I64ToI16WriterJvm")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I64ToI16Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    buffer.putShort(address, value.toShort())
}
