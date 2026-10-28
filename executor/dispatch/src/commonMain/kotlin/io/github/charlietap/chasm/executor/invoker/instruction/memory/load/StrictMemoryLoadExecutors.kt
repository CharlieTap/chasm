package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import io.github.charlietap.chasm.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.operations.effectiveAddress
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeMemoryLoad(
    vstack: ValueStack,
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    destinationSlot: Int,
    bytes: Int,
    crossinline operation: (Int) -> Long,
) {
    val effectiveAddress = effectiveAddress(address, offset)
    val result = OptimisticBoundsChecker(effectiveAddress, bytes, memory.size) {
        operation(effectiveAddress)
    }

    vstack.setFrameSlot(destinationSlot, result)
}
