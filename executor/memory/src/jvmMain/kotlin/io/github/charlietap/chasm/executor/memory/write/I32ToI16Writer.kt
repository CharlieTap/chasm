@file:JvmName("I32ToI16WriterJvm")

package io.github.charlietap.chasm.executor.memory.write

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.ext.copyInto
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun I32ToI16Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
) {
    val byteArray = (memory as ByteArrayLinearMemory).memory
    value.copyInto(byteArray, address, 2)
}
