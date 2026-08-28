package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayGetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayGetDispatcher(
    instruction: AggregateInstruction.ArrayGet,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArrayGetExecutor(vstack, context, instruction)
    nextIp
}
