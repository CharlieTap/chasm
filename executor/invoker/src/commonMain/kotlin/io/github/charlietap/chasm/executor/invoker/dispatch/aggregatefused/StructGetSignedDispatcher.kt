package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructGetSignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun StructGetSignedDispatcher(
    instruction: FusedAggregateInstruction.StructGetSigned,
) = StructGetSignedDispatcher(
    instruction = instruction,
    executor = ::StructGetSignedExecutor,
)

internal inline fun StructGetSignedDispatcher(
    instruction: FusedAggregateInstruction.StructGetSigned,
    crossinline executor: Executor<FusedAggregateInstruction.StructGetSigned>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
