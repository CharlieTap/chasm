package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayGetSignedExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

fun ArrayGetSignedDispatcher(
    instruction: AggregateInstruction.ArrayGetSigned,
) = ArrayGetSignedDispatcher(
    instruction = instruction,
    executor = ::ArrayGetSignedExecutor,
)

internal inline fun ArrayGetSignedDispatcher(
    instruction: AggregateInstruction.ArrayGetSigned,
    crossinline executor: Executor<AggregateInstruction.ArrayGetSigned>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}