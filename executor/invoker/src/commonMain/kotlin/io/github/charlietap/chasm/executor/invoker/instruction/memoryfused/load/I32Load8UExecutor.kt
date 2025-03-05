package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.I328UReader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I32Load8UExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Load8U,
) =
    I32Load8UExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I328UReader,
    )

internal inline fun I32Load8UExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Load8U,
    crossinline boundsChecker: BoundsChecker<Int>,
    crossinline reader: I328UReader,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val baseAddress = instruction.address(stack).toInt()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val result = boundsChecker(effectiveAddress, 1, memory.size) {
        reader(memory.data, effectiveAddress)
    }

    instruction.destination(result.toLong(), stack)
}
