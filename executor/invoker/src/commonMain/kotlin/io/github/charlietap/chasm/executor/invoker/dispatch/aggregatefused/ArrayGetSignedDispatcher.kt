package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayGetSignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun ArrayGetSignedDispatcher(
    instruction: FusedAggregateInstruction.ArrayGetSigned,
) = ArrayGetSignedDispatcher(
    instruction = instruction,
    executor = ::ArrayGetSignedExecutor,
)

internal inline fun ArrayGetSignedDispatcher(
    instruction: FusedAggregateInstruction.ArrayGetSigned,
    crossinline executor: Executor<FusedAggregateInstruction.ArrayGetSigned>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
