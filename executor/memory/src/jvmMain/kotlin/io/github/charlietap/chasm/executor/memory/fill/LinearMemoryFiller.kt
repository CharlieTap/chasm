@file:JvmName("LinearMemoryFillerJvm")

package io.github.charlietap.chasm.executor.memory.fill

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

actual inline fun LinearMemoryFiller(
    memory: LinearMemory,
    address: Int,
    bytesToFill: Int,
    fillValue: Byte,
) {
    val byteArray = (memory as ByteArrayLinearMemory)
    byteArray.memory.fill(fillValue, address, address + bytesToFill)
}
