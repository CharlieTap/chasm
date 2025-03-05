package io.github.charlietap.chasm.memory.fill

import io.github.charlietap.chasm.memory.NativeLinearMemory
import io.github.charlietap.chasm.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import liblinmem.fill

actual inline fun LinearMemoryFiller(
    memory: LinearMemory,
    address: Int,
    bytesToFill: Int,
    fillValue: Byte,
    upperBound: Int,
) {
    val memory = (memory as NativeLinearMemory)
    OptimisticBoundsChecker(address, bytesToFill, upperBound) {
        fill(memory.pointer, address, bytesToFill, fillValue.toUByte())
    }
}
