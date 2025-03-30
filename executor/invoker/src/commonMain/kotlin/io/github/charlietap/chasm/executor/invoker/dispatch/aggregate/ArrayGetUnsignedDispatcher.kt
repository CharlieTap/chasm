package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayGetUnsignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayGetUnsignedDispatcher(
    instruction: AggregateInstruction.ArrayGetUnsigned,
) = ArrayGetUnsignedDispatcher(
    instruction = instruction,
    executor = ::ArrayGetUnsignedExecutor,
)

internal inline fun ArrayGetUnsignedDispatcher(
    instruction: AggregateInstruction.ArrayGetUnsigned,
    crossinline executor: Executor<AggregateInstruction.ArrayGetUnsigned>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
