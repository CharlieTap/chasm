@file:JvmName("F64WriterJvm")

package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun F64Writer(
    memory: LinearMemory,
    address: Int,
    value: Double,
) {
    val buffer = (memory as ByteBufferLinearMemory).memory
    buffer.putDouble(address, value)
}
