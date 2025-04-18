package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructGetUnsignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun StructGetUnsignedDispatcher(
    instruction: AggregateInstruction.StructGetUnsigned,
) = StructGetUnsignedDispatcher(
    instruction = instruction,
    executor = ::StructGetUnsignedExecutor,
)

internal inline fun StructGetUnsignedDispatcher(
    instruction: AggregateInstruction.StructGetUnsigned,
    crossinline executor: Executor<AggregateInstruction.StructGetUnsigned>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
