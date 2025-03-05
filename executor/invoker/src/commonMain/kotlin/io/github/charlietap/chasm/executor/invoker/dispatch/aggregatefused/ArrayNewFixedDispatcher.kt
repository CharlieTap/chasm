package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayNewFixedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun ArrayNewFixedDispatcher(
    instruction: FusedAggregateInstruction.ArrayNewFixed,
) = ArrayNewFixedDispatcher(
    instruction = instruction,
    executor = ::ArrayNewFixedExecutor,
)

internal inline fun ArrayNewFixedDispatcher(
    instruction: FusedAggregateInstruction.ArrayNewFixed,
    crossinline executor: Executor<FusedAggregateInstruction.ArrayNewFixed>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
