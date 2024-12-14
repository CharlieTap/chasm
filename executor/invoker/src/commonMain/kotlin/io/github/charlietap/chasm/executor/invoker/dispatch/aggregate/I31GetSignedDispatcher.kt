package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.I31GetSignedExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

fun I31GetSignedDispatcher(
    instruction: AggregateInstruction.I31GetSigned,
) = I31GetSignedDispatcher(
    instruction = instruction,
    executor = ::I31GetSignedExecutor,
)

internal inline fun I31GetSignedDispatcher(
    instruction: AggregateInstruction.I31GetSigned,
    crossinline executor: Executor<AggregateInstruction.I31GetSigned>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
