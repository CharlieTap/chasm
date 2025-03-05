package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.I6432SReader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

internal inline fun I64Load32SExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Load32S,
) =
    I64Load32SExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I6432SReader,
    )

internal inline fun I64Load32SExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Load32S,
    crossinline boundsChecker: BoundsChecker<Long>,
    crossinline reader: I6432SReader,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val baseAddress = instruction.address(stack).toInt()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val result = boundsChecker(effectiveAddress, 4, memory.size) {
        reader(memory.data, effectiveAddress)
    }

    instruction.destination(result, stack)
}
