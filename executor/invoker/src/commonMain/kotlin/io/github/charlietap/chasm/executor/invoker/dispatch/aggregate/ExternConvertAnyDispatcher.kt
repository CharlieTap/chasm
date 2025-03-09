package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ExternConvertAnyExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ExternConvertAnyDispatcher(
    instruction: AggregateInstruction.ExternConvertAny,
) = ExternConvertAnyDispatcher(
    instruction = instruction,
    executor = ::ExternConvertAnyExecutor,
)

internal inline fun ExternConvertAnyDispatcher(
    instruction: AggregateInstruction.ExternConvertAny,
    crossinline executor: Executor<AggregateInstruction.ExternConvertAny>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
