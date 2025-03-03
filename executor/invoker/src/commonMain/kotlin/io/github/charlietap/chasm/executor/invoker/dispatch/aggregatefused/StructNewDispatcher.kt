package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructNewExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction

fun StructNewDispatcher(
    instruction: FusedAggregateInstruction.StructNew,
) = StructNewDispatcher(
    instruction = instruction,
    executor = ::StructNewExecutor,
)

internal inline fun StructNewDispatcher(
    instruction: FusedAggregateInstruction.StructNew,
    crossinline executor: Executor<FusedAggregateInstruction.StructNew>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
