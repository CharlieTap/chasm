package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructNewExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun StructNewDispatcher(
    instruction: AggregateInstruction.StructNew,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    StructNewExecutor(vstack, context, instruction)
    nextIp
}
