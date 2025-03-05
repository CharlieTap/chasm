@file:JvmName("LinearMemoryFillerJvm")

package io.github.charlietap.chasm.executor.memory.fill

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun LinearMemoryFiller(
    memory: LinearMemory,
    address: Int,
    bytesToFill: Int,
    fillValue: Byte,
    upperBound: Int,
) {
    val byteArray = (memory as ByteArrayLinearMemory)
    OptimisticBoundsChecker(address, bytesToFill, upperBound) {
        byteArray.memory.fill(fillValue, address, address + bytesToFill)
    }
}
