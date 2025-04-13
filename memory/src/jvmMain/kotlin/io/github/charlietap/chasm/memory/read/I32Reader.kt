@file:JvmName("I32ReaderJvm")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32Reader(
    memory: LinearMemory,
    address: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return buffer.getInt(address)
}
