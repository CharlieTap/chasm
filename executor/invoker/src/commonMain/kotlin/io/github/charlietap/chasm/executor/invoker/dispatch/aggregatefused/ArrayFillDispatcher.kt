package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayFillExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction

fun ArrayFillDispatcher(
    instruction: FusedAggregateInstruction.ArrayFill,
) = ArrayFillDispatcher(
    instruction = instruction,
    executor = ::ArrayFillExecutor,
)

internal inline fun ArrayFillDispatcher(
    instruction: FusedAggregateInstruction.ArrayFill,
    crossinline executor: Executor<FusedAggregateInstruction.ArrayFill>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
