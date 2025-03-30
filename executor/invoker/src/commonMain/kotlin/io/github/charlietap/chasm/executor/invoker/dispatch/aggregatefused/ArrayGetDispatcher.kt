package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayGetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun ArrayGetDispatcher(
    instruction: FusedAggregateInstruction.ArrayGet,
) = ArrayGetDispatcher(
    instruction = instruction,
    executor = ::ArrayGetExecutor,
)

internal inline fun ArrayGetDispatcher(
    instruction: FusedAggregateInstruction.ArrayGet,
    crossinline executor: Executor<FusedAggregateInstruction.ArrayGet>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
