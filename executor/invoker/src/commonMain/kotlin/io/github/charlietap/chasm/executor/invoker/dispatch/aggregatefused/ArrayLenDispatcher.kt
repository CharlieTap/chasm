package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayLenExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun ArrayLenDispatcher(
    instruction: FusedAggregateInstruction.ArrayLen,
) = ArrayLenDispatcher(
    instruction = instruction,
    executor = ::ArrayLenExecutor,
)

internal inline fun ArrayLenDispatcher(
    instruction: FusedAggregateInstruction.ArrayLen,
    crossinline executor: Executor<FusedAggregateInstruction.ArrayLen>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
