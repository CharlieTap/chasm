@file:JvmName("1648UReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I648UReader(
    memory: LinearMemory,
    address: Int,
): Long {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray[address].toUByte().toLong()
    return value
}
