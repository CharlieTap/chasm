package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayInitElementExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayInitElementDispatcher(
    instruction: AggregateInstruction.ArrayInitElement,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArrayInitElementExecutor(vstack, context, instruction)
    nextIp
}
