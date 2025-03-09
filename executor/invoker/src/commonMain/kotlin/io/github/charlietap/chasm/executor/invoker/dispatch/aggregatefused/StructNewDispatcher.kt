package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructNewExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun StructNewDispatcher(
    instruction: FusedAggregateInstruction.StructNew,
) = StructNewDispatcher(
    instruction = instruction,
    executor = ::StructNewExecutor,
)

internal inline fun StructNewDispatcher(
    instruction: FusedAggregateInstruction.StructNew,
    crossinline executor: Executor<FusedAggregateInstruction.StructNew>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
