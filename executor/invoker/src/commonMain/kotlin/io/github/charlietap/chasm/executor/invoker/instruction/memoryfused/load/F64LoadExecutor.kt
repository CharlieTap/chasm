package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.F64Reader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedMemoryInstruction

fun F64LoadExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F64Load,
) =
    F64LoadExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::F64Reader,
    )

internal inline fun F64LoadExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F64Load,
    crossinline boundsChecker: BoundsChecker<Double>,
    crossinline reader: F64Reader,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val baseAddress = instruction.address(stack).toInt()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val result = boundsChecker(effectiveAddress, Double.SIZE_BYTES, memory.size) {
        reader(memory.data, effectiveAddress)
    }

    instruction.destination(result.toLong(), stack)
}
