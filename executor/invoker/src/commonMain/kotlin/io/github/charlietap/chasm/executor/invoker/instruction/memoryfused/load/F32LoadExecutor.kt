package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.F32Reader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedMemoryInstruction

fun F32LoadExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F32Load,
) =
    F32LoadExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::F32Reader,
    )

internal inline fun F32LoadExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F32Load,
    crossinline boundsChecker: BoundsChecker<Float>,
    crossinline reader: F32Reader,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val baseAddress = instruction.address(stack).toInt()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val result = boundsChecker(effectiveAddress, Float.SIZE_BYTES, memory.size) {
        reader(memory.data, effectiveAddress)
    }

    instruction.destination(result.toLong(), stack)
}
