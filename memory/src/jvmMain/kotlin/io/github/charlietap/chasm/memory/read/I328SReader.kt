@file:JvmName("I328SReaderJvm")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I328SReader(
    memory: LinearMemory,
    address: Int,
): Int {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return buffer.get(address).toInt()
}
