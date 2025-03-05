package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewFixedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayNewFixedDispatcher(
    instruction: AggregateInstruction.ArrayNewFixed,
) = ArrayNewFixedDispatcher(
    instruction = instruction,
    executor = ::ArrayNewFixedExecutor,
)

internal inline fun ArrayNewFixedDispatcher(
    instruction: AggregateInstruction.ArrayNewFixed,
    crossinline executor: Executor<AggregateInstruction.ArrayNewFixed>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
