@file:JvmName("I32WriterJvm")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    buffer.putInt(address, value)
}
