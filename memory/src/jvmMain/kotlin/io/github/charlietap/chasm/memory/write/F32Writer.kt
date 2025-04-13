@file:JvmName("F32WriterJvm")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun F32Writer(
    memory: LinearMemory,
    address: Int,
    value: Float,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    buffer.putFloat(address, value)
}
