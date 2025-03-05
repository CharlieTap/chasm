package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayFillExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayFillDispatcher(
    instruction: AggregateInstruction.ArrayFill,
) = ArrayFillDispatcher(
    instruction = instruction,
    executor = ::ArrayFillExecutor,
)

internal inline fun ArrayFillDispatcher(
    instruction: AggregateInstruction.ArrayFill,
    crossinline executor: Executor<AggregateInstruction.ArrayFill>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
