package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayLenExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

fun ArrayLenDispatcher(
    instruction: AggregateInstruction.ArrayLen,
) = ArrayLenDispatcher(
    instruction = instruction,
    executor = ::ArrayLenExecutor,
)

internal inline fun ArrayLenDispatcher(
    instruction: AggregateInstruction.ArrayLen,
    crossinline executor: Executor<AggregateInstruction.ArrayLen>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
