package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayCopyExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayCopyDispatcher(
    instruction: AggregateInstruction.ArrayCopy,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArrayCopyExecutor(vstack, context, instruction)
    nextIp
}
