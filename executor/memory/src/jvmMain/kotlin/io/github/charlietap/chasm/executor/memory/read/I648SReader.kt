@file:JvmName("I648SReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun I648SReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray[address].toLong()
    return value
}
