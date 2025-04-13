@file:JvmName("I648SReaderJvm")

package io.github.charlietap.chasm.memory.read

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I648SReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val buffer = (memory as ByteBufferLinearMemory).memory
    return buffer.get(address).toLong()
}
