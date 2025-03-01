package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayGetUnsignedExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction

fun ArrayGetUnsignedDispatcher(
    instruction: FusedAggregateInstruction.ArrayGetUnsigned,
) = ArrayGetUnsignedDispatcher(
    instruction = instruction,
    executor = ::ArrayGetUnsignedExecutor,
)

internal inline fun ArrayGetUnsignedDispatcher(
    instruction: FusedAggregateInstruction.ArrayGetUnsigned,
    crossinline executor: Executor<FusedAggregateInstruction.ArrayGetUnsigned>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
