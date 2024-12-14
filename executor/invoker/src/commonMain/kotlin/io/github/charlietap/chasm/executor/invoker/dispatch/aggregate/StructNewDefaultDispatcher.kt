package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructNewDefaultExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

fun StructNewDefaultDispatcher(
    instruction: AggregateInstruction.StructNewDefault,
) = StructNewDefaultDispatcher(
    instruction = instruction,
    executor = ::StructNewDefaultExecutor,
)

internal inline fun StructNewDefaultDispatcher(
    instruction: AggregateInstruction.StructNewDefault,
    crossinline executor: Executor<AggregateInstruction.StructNewDefault>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}