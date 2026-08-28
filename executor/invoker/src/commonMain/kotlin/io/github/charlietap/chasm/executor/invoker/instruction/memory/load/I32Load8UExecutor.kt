package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.memory.read.I328UReader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

fun I32Load8UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load8U,
) =
    I32Load8UExecutor(
        vstack = vstack,
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I328UReader,
    )

internal inline fun I32Load8UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load8U,
    crossinline boundsChecker: BoundsChecker<Int>,
    crossinline reader: I328UReader,
) {
    val memory = instruction.memory

    val baseAddress = vstack.popI32()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val result = boundsChecker(effectiveAddress, 1, memory.size) {
        reader(memory.data, effectiveAddress)
    }

    vstack.pushI32(result)
}
