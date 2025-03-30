package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayCopyExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun ArrayCopyDispatcher(
    instruction: FusedAggregateInstruction.ArrayCopy,
) = ArrayCopyDispatcher(
    instruction = instruction,
    executor = ::ArrayCopyExecutor,
)

internal inline fun ArrayCopyDispatcher(
    instruction: FusedAggregateInstruction.ArrayCopy,
    crossinline executor: Executor<FusedAggregateInstruction.ArrayCopy>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
