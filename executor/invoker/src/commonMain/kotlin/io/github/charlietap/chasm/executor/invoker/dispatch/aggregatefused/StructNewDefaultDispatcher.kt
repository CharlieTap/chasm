package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructNewDefaultExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun StructNewDefaultDispatcher(
    instruction: FusedAggregateInstruction.StructNewDefault,
) = StructNewDefaultDispatcher(
    instruction = instruction,
    executor = ::StructNewDefaultExecutor,
)

internal inline fun StructNewDefaultDispatcher(
    instruction: FusedAggregateInstruction.StructNewDefault,
    crossinline executor: Executor<FusedAggregateInstruction.StructNewDefault>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
