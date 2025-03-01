package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructGetExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction

fun StructGetDispatcher(
    instruction: FusedAggregateInstruction.StructGet,
) = StructGetDispatcher(
    instruction = instruction,
    executor = ::StructGetExecutor,
)

internal inline fun StructGetDispatcher(
    instruction: FusedAggregateInstruction.StructGet,
    crossinline executor: Executor<FusedAggregateInstruction.StructGet>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
