package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayInitElementExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayInitElementDispatcher(
    instruction: AggregateInstruction.ArrayInitElement,
) = ArrayInitElementDispatcher(
    instruction = instruction,
    executor = ::ArrayInitElementExecutor,
)

internal inline fun ArrayInitElementDispatcher(
    instruction: AggregateInstruction.ArrayInitElement,
    crossinline executor: Executor<AggregateInstruction.ArrayInitElement>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
