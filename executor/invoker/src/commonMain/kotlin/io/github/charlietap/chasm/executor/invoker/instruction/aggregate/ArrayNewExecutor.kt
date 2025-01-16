package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun ArrayNewExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNew,
) =
    ArrayNewExecutor(
        context = context,
        instruction = instruction,
        arrayNewFixedExecutor = ::ArrayNewFixedExecutor,
    )

internal inline fun ArrayNewExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNew,
    crossinline arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed>,
) {

    val (stack) = context
    val typeIndex = instruction.typeIndex
    val size = stack.popI32()
    val value = stack.peekValue()
    repeat(size - 1) {
        stack.push(value)
    }

    arrayNewFixedExecutor(context, AggregateInstruction.ArrayNewFixed(typeIndex, size.toUInt()))
}
