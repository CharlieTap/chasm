package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArraySetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArraySetDispatcher(
    instruction: AggregateInstruction.ArraySet,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArraySetExecutor(vstack, context, instruction)
    nextIp
}
