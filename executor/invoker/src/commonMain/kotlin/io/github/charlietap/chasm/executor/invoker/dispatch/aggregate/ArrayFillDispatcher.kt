package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayFillExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayFillDispatcher(
    instruction: AggregateInstruction.ArrayFill,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArrayFillExecutor(vstack, context, instruction)
    nextIp
}
