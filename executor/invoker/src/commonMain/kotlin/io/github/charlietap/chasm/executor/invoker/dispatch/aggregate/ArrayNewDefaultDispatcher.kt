package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewDefaultExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayNewDefaultDispatcher(
    instruction: AggregateInstruction.ArrayNewDefault,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArrayNewDefaultExecutor(vstack, context, instruction)
    nextIp
}
