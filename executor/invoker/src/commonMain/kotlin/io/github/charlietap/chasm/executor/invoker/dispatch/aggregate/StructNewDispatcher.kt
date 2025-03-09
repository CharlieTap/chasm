package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructNewExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun StructNewDispatcher(
    instruction: AggregateInstruction.StructNew,
) = StructNewDispatcher(
    instruction = instruction,
    executor = ::StructNewExecutor,
)

internal inline fun StructNewDispatcher(
    instruction: AggregateInstruction.StructNew,
    crossinline executor: Executor<AggregateInstruction.StructNew>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
