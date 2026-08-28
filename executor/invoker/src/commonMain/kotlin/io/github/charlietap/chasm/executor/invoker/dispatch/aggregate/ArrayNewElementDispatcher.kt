package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewElementExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayNewElementDispatcher(
    instruction: AggregateInstruction.ArrayNewElement,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArrayNewElementExecutor(vstack, context, instruction)
    nextIp
}
