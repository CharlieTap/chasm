package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.memory.read.I3216SReader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I32Load16SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Load16S,
) =
    I32Load16SExecutor(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I3216SReader,
    )

internal inline fun I32Load16SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Load16S,
    crossinline boundsChecker: BoundsChecker<Int>,
    crossinline reader: I3216SReader,
) {
    val memory = instruction.memory

    val baseAddress = instruction.address(vstack).toInt()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val result = boundsChecker(effectiveAddress, 2, memory.size) {
        reader(memory.data, effectiveAddress)
    }

    instruction.destination(result.toLong(), vstack)
}
