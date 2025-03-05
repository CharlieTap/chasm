package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayNewExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun ArrayNewDispatcher(
    instruction: FusedAggregateInstruction.ArrayNew,
) = ArrayNewDispatcher(
    instruction = instruction,
    executor = ::ArrayNewExecutor,
)

internal inline fun ArrayNewDispatcher(
    instruction: FusedAggregateInstruction.ArrayNew,
    crossinline executor: Executor<FusedAggregateInstruction.ArrayNew>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
