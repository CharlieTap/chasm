package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.memory.read.I328SReader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32Load8SExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load8S,
) =
    I32Load8SExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I328SReader,
    )

internal inline fun I32Load8SExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load8S,
    crossinline boundsChecker: BoundsChecker<Int>,
    crossinline reader: I328SReader,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val baseAddress = stack.popI32()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val result = boundsChecker(effectiveAddress, 1, memory.size) {
        reader(memory.data, effectiveAddress)
    }

    stack.pushI32(result)
}
