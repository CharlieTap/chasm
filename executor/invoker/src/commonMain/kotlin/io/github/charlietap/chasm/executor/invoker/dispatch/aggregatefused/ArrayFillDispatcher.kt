package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayFillExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun ArrayFillDispatcher(
    instruction: FusedAggregateInstruction.ArrayFill,
) = ArrayFillDispatcher(
    instruction = instruction,
    executor = ::ArrayFillExecutor,
)

internal inline fun ArrayFillDispatcher(
    instruction: FusedAggregateInstruction.ArrayFill,
    crossinline executor: Executor<FusedAggregateInstruction.ArrayFill>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
