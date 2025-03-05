package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.memory.read.I64Reader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I64LoadExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Load,
) =
    I64LoadExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I64Reader,
    )

internal inline fun I64LoadExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Load,
    crossinline boundsChecker: BoundsChecker<Long>,
    crossinline reader: I64Reader,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val baseAddress = instruction.address(stack).toInt()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val result = boundsChecker(effectiveAddress, Long.SIZE_BYTES, memory.size) {
        reader(memory.data, effectiveAddress)
    }

    instruction.destination(result, stack)
}
