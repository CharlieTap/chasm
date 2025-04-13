@file:JvmName("F32ReaderJvm")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun F32Reader(
    memory: LinearMemory,
    address: Int,
): Float {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return buffer.getFloat(address)
}
