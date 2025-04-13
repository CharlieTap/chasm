@file:JvmName("I64ToI8WriterJvm")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I64ToI8Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    buffer.put(address, value.toByte())
}
