@file:JvmName("I64ToI32WriterJvm")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I64ToI32Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    buffer.putInt(address, value.toInt())
}
