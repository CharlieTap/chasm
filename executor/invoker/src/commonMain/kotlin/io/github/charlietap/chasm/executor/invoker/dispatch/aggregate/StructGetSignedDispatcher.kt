package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructGetSignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun StructGetSignedDispatcher(
    instruction: AggregateInstruction.StructGetSigned,
) = StructGetSignedDispatcher(
    instruction = instruction,
    executor = ::StructGetSignedExecutor,
)

internal inline fun StructGetSignedDispatcher(
    instruction: AggregateInstruction.StructGetSigned,
    crossinline executor: Executor<AggregateInstruction.StructGetSigned>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
