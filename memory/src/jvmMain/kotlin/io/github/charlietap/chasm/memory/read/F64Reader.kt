@file:JvmName("F64ReaderJvm")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun F64Reader(
    memory: LinearMemory,
    address: Int,
): Double {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return buffer.getDouble(address)
}
