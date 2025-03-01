package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructGetUnsignedExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction

fun StructGetUnsignedDispatcher(
    instruction: FusedAggregateInstruction.StructGetUnsigned,
) = StructGetUnsignedDispatcher(
    instruction = instruction,
    executor = ::StructGetUnsignedExecutor,
)

internal inline fun StructGetUnsignedDispatcher(
    instruction: FusedAggregateInstruction.StructGetUnsigned,
    crossinline executor: Executor<FusedAggregateInstruction.StructGetUnsigned>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
