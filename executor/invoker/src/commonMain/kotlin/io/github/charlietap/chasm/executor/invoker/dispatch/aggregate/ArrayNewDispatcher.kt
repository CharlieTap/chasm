package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

fun ArrayNewDispatcher(
    instruction: AggregateInstruction.ArrayNew,
) = ArrayNewDispatcher(
    instruction = instruction,
    executor = ::ArrayNewExecutor,
)

internal inline fun ArrayNewDispatcher(
    instruction: AggregateInstruction.ArrayNew,
    crossinline executor: Executor<AggregateInstruction.ArrayNew>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
