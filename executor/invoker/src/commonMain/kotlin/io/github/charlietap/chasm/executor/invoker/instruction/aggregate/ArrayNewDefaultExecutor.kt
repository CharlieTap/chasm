package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun ArrayNewDefaultExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewDefault,
) =
    ArrayNewDefaultExecutor(
        context = context,
        instruction = instruction,
        arrayNewFixedExecutor = ::ArrayNewFixedExecutor,
    )

internal inline fun ArrayNewDefaultExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewDefault,
    crossinline arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed>,
) {
    val stack = context.vstack
    val size = stack.popI32()

    repeat(size) {
        stack.push(instruction.field)
    }

    arrayNewFixedExecutor(
        context,
        AggregateInstruction.ArrayNewFixed(
            definedType = instruction.definedType,
            arrayType = instruction.arrayType,
            size = size.toUInt(),
        ),
    )
}
