package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayNewDispatcher(
    instruction: AggregateInstruction.ArrayNew,
) = ArrayNewDispatcher(
    instruction = instruction,
    executor = ::ArrayNewExecutor,
)

internal inline fun ArrayNewDispatcher(
    instruction: AggregateInstruction.ArrayNew,
    crossinline executor: Executor<AggregateInstruction.ArrayNew>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
