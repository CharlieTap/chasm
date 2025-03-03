package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructNewDefaultExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction

fun StructNewDefaultDispatcher(
    instruction: FusedAggregateInstruction.StructNewDefault,
) = StructNewDefaultDispatcher(
    instruction = instruction,
    executor = ::StructNewDefaultExecutor,
)

internal inline fun StructNewDefaultDispatcher(
    instruction: FusedAggregateInstruction.StructNewDefault,
    crossinline executor: Executor<FusedAggregateInstruction.StructNewDefault>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
