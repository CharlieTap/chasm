package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewDataExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayNewDataDispatcher(
    instruction: AggregateInstruction.ArrayNewData,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArrayNewDataExecutor(vstack, context, instruction)
    nextIp
}
