package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayLenExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayLenDispatcher(
    instruction: AggregateInstruction.ArrayLen,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArrayLenExecutor(vstack, context, instruction)
    nextIp
}
