package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun memoryLoadNoOffsetDispatcher(
    memory: MemoryInstance,
    destinationSlot: Int,
    bytes: Int,
    crossinline address: (ValueStack) -> Int,
    crossinline load: (LinearMemory, Int) -> Long,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    val effectiveAddress = address(vstack)
    if (effectiveAddress < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val result = OptimisticBoundsChecker(effectiveAddress, bytes, memory.size) {
        load(memory.data, effectiveAddress)
    }
    vstack.setFrameSlot(destinationSlot, result)
    nextIp
}

internal inline fun memoryStoreNoOffsetDispatcher(
    memory: MemoryInstance,
    bytes: Int,
    crossinline address: (ValueStack) -> Int,
    crossinline store: (ValueStack, LinearMemory, Int) -> Unit,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    val effectiveAddress = address(vstack)
    OptimisticBoundsChecker(effectiveAddress, bytes, memory.size) {
        store(vstack, memory.data, effectiveAddress)
    }
    nextIp
}
