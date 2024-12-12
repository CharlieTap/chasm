package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayCopyExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

fun ArrayCopyDispatcher(
    instruction: AggregateInstruction.ArrayCopy,
) = ArrayCopyDispatcher(
    instruction = instruction,
    executor = ::ArrayCopyExecutor,
)

internal inline fun ArrayCopyDispatcher(
    instruction: AggregateInstruction.ArrayCopy,
    crossinline executor: Executor<AggregateInstruction.ArrayCopy>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
