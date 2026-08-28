package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewFixedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayNewFixedDispatcher(
    instruction: AggregateInstruction.ArrayNewFixed,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArrayNewFixedExecutor(vstack, context, instruction)
    nextIp
}
