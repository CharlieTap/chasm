package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructNewExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

fun StructNewDispatcher(
    instruction: AggregateInstruction.StructNew,
) = StructNewDispatcher(
    instruction = instruction,
    executor = ::StructNewExecutor,
)

internal inline fun StructNewDispatcher(
    instruction: AggregateInstruction.StructNew,
    crossinline executor: Executor<AggregateInstruction.StructNew>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
