package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.I3216UReader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedMemoryInstruction

internal inline fun I32Load16UExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Load16U,
) =
    I32Load16UExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I3216UReader,
    )

internal inline fun I32Load16UExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Load16U,
    crossinline boundsChecker: BoundsChecker<Int>,
    crossinline reader: I3216UReader,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val baseAddress = instruction.address(stack).toInt()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val result = boundsChecker(effectiveAddress, 2, memory.size) {
        reader(memory.data, effectiveAddress)
    }

    instruction.destination(result.toLong(), stack)
}
