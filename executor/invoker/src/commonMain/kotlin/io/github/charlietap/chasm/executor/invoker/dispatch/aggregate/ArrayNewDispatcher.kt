package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayNewDispatcher(
    instruction: AggregateInstruction.ArrayNew,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArrayNewExecutor(vstack, context, instruction)
    nextIp
}
