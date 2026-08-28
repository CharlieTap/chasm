package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructGetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun StructGetDispatcher(
    instruction: AggregateInstruction.StructGet,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    StructGetExecutor(vstack, context, instruction)
    nextIp
}
