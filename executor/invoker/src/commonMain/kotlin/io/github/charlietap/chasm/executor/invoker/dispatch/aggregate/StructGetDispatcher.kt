package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructGetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun StructGetDispatcher(
    instruction: AggregateInstruction.StructGet,
) = StructGetDispatcher(
    instruction = instruction,
    executor = ::StructGetExecutor,
)

internal inline fun StructGetDispatcher(
    instruction: AggregateInstruction.StructGet,
    crossinline executor: Executor<AggregateInstruction.StructGet>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
