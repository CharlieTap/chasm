@file:JvmName("I328SReaderJvm")

package io.github.charlietap.chasm.executor.memory.read

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I328SReader(
    memory: LinearMemory,
    address: Int,
): Int {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    val value = byteArray[address].toInt()
    return value
}
