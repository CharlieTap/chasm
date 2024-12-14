package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewDefaultExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

fun ArrayNewDefaultDispatcher(
    instruction: AggregateInstruction.ArrayNewDefault,
) = ArrayNewDefaultDispatcher(
    instruction = instruction,
    executor = ::ArrayNewDefaultExecutor,
)

internal inline fun ArrayNewDefaultDispatcher(
    instruction: AggregateInstruction.ArrayNewDefault,
    crossinline executor: Executor<AggregateInstruction.ArrayNewDefault>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
