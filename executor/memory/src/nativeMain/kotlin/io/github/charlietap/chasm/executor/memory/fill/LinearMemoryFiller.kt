package io.github.charlietap.chasm.executor.memory.fill

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
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
