package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.I31GetSignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun I31GetSignedDispatcher(
    instruction: AggregateInstruction.I31GetSigned,
) = I31GetSignedDispatcher(
    instruction = instruction,
    executor = ::I31GetSignedExecutor,
)

internal inline fun I31GetSignedDispatcher(
    instruction: AggregateInstruction.I31GetSigned,
    crossinline executor: Executor<AggregateInstruction.I31GetSigned>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
