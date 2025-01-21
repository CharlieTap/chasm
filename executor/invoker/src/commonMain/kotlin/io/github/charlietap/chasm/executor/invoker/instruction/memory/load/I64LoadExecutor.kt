package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.I64Reader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushI64
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun I64LoadExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load,
) =
    I64LoadExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I64Reader,
    )

internal inline fun I64LoadExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load,
    crossinline boundsChecker: BoundsChecker<Long>,
    crossinline reader: I64Reader,
) {
    val stack = context.stack
    val memory = instruction.memory

    val baseAddress = stack.popI32()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val result = boundsChecker(effectiveAddress, Long.SIZE_BYTES, memory.size) {
        reader(memory.data, effectiveAddress)
    }

    stack.pushI64(result)
}
