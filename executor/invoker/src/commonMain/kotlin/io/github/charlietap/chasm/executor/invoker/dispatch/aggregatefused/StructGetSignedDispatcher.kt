package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructGetSignedExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction

fun StructGetSignedDispatcher(
    instruction: FusedAggregateInstruction.StructGetSigned,
) = StructGetSignedDispatcher(
    instruction = instruction,
    executor = ::StructGetSignedExecutor,
)

internal inline fun StructGetSignedDispatcher(
    instruction: FusedAggregateInstruction.StructGetSigned,
    crossinline executor: Executor<FusedAggregateInstruction.StructGetSigned>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
