package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.operations.effectiveAddress

internal inline fun executeMemoryStore(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    bytes: Int,
    crossinline operation: (Int) -> Unit,
) {
    val effectiveAddress = effectiveAddress(address, offset)
    OptimisticBoundsChecker(effectiveAddress, bytes, memory.size) {
        operation(effectiveAddress)
    }
}
