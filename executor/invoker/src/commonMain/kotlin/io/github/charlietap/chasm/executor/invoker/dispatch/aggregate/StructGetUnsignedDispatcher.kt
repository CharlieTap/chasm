package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructGetUnsignedExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

fun StructGetUnsignedDispatcher(
    instruction: AggregateInstruction.StructGetUnsigned,
) = StructGetUnsignedDispatcher(
    instruction = instruction,
    executor = ::StructGetUnsignedExecutor,
)

internal inline fun StructGetUnsignedDispatcher(
    instruction: AggregateInstruction.StructGetUnsigned,
    crossinline executor: Executor<AggregateInstruction.StructGetUnsigned>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
