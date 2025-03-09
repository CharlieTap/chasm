package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructGetUnsignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun StructGetUnsignedDispatcher(
    instruction: FusedAggregateInstruction.StructGetUnsigned,
) = StructGetUnsignedDispatcher(
    instruction = instruction,
    executor = ::StructGetUnsignedExecutor,
)

internal inline fun StructGetUnsignedDispatcher(
    instruction: FusedAggregateInstruction.StructGetUnsigned,
    crossinline executor: Executor<FusedAggregateInstruction.StructGetUnsigned>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
