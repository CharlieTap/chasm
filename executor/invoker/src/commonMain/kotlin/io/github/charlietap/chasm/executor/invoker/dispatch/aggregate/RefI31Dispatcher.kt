package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.RefI31Executor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

fun RefI31Dispatcher(
    instruction: AggregateInstruction.RefI31,
) = RefI31Dispatcher(
    instruction = instruction,
    executor = ::RefI31Executor,
)

internal inline fun RefI31Dispatcher(
    instruction: AggregateInstruction.RefI31,
    crossinline executor: Executor<AggregateInstruction.RefI31>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
