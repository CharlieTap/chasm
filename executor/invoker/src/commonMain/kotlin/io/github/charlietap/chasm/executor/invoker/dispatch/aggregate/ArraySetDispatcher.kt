package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArraySetExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

fun ArraySetDispatcher(
    instruction: AggregateInstruction.ArraySet,
) = ArraySetDispatcher(
    instruction = instruction,
    executor = ::ArraySetExecutor,
)

internal inline fun ArraySetDispatcher(
    instruction: AggregateInstruction.ArraySet,
    crossinline executor: Executor<AggregateInstruction.ArraySet>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
