package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayGetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayGetDispatcher(
    instruction: AggregateInstruction.ArrayGet,
) = ArrayGetDispatcher(
    instruction = instruction,
    executor = ::ArrayGetExecutor,
)

internal inline fun ArrayGetDispatcher(
    instruction: AggregateInstruction.ArrayGet,
    crossinline executor: Executor<AggregateInstruction.ArrayGet>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
