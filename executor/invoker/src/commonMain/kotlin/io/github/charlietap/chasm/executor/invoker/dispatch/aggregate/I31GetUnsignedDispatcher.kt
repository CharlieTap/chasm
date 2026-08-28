package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.I31GetUnsignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun I31GetUnsignedDispatcher(
    instruction: AggregateInstruction.I31GetUnsigned,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I31GetUnsignedExecutor(vstack, context, instruction)
    nextIp
}
