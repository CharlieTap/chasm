package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.I31GetUnsignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun I31GetUnsignedDispatcher(
    instruction: AggregateInstruction.I31GetUnsigned,
) = I31GetUnsignedDispatcher(
    instruction = instruction,
    executor = ::I31GetUnsignedExecutor,
)

internal inline fun I31GetUnsignedDispatcher(
    instruction: AggregateInstruction.I31GetUnsigned,
    crossinline executor: Executor<AggregateInstruction.I31GetUnsigned>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
