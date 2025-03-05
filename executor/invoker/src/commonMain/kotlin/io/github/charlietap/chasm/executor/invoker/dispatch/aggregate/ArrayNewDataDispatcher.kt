package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewDataExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayNewDataDispatcher(
    instruction: AggregateInstruction.ArrayNewData,
) = ArrayNewDataDispatcher(
    instruction = instruction,
    executor = ::ArrayNewDataExecutor,
)

internal inline fun ArrayNewDataDispatcher(
    instruction: AggregateInstruction.ArrayNewData,
    crossinline executor: Executor<AggregateInstruction.ArrayNewData>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
