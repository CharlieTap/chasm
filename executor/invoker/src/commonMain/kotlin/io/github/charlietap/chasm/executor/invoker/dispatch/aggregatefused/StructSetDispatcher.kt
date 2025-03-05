package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructSetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun StructSetDispatcher(
    instruction: FusedAggregateInstruction.StructSet,
) = StructSetDispatcher(
    instruction = instruction,
    executor = ::StructSetExecutor,
)

internal inline fun StructSetDispatcher(
    instruction: FusedAggregateInstruction.StructSet,
    crossinline executor: Executor<FusedAggregateInstruction.StructSet>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
