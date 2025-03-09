package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewElementExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayNewElementDispatcher(
    instruction: AggregateInstruction.ArrayNewElement,
) = ArrayNewElementDispatcher(
    instruction = instruction,
    executor = ::ArrayNewElementExecutor,
)

internal inline fun ArrayNewElementDispatcher(
    instruction: AggregateInstruction.ArrayNewElement,
    crossinline executor: Executor<AggregateInstruction.ArrayNewElement>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
