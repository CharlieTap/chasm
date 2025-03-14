package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayGetUnsignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun ArrayGetUnsignedDispatcher(
    instruction: FusedAggregateInstruction.ArrayGetUnsigned,
) = ArrayGetUnsignedDispatcher(
    instruction = instruction,
    executor = ::ArrayGetUnsignedExecutor,
)

internal inline fun ArrayGetUnsignedDispatcher(
    instruction: FusedAggregateInstruction.ArrayGetUnsigned,
    crossinline executor: Executor<FusedAggregateInstruction.ArrayGetUnsigned>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
