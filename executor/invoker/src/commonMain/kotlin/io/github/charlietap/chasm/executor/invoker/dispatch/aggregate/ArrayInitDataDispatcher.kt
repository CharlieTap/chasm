package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayInitDataExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

fun ArrayInitDataDispatcher(
    instruction: AggregateInstruction.ArrayInitData,
) = ArrayInitDataDispatcher(
    instruction = instruction,
    executor = ::ArrayInitDataExecutor,
)

internal inline fun ArrayInitDataDispatcher(
    instruction: AggregateInstruction.ArrayInitData,
    crossinline executor: Executor<AggregateInstruction.ArrayInitData>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
