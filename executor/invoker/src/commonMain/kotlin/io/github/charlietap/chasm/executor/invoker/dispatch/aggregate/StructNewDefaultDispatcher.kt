package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructNewDefaultExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun StructNewDefaultDispatcher(
    instruction: AggregateInstruction.StructNewDefault,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    StructNewDefaultExecutor(vstack, context, instruction)
    nextIp
}
