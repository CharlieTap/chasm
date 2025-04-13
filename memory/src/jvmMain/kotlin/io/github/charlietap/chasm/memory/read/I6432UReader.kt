@file:JvmName("I6432UReaderJvm")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I6432UReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return buffer.getInt(address).toUInt().toLong()
}
