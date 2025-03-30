package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArraySetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun ArraySetDispatcher(
    instruction: FusedAggregateInstruction.ArraySet,
) = ArraySetDispatcher(
    instruction = instruction,
    executor = ::ArraySetExecutor,
)

internal inline fun ArraySetDispatcher(
    instruction: FusedAggregateInstruction.ArraySet,
    crossinline executor: Executor<FusedAggregateInstruction.ArraySet>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
