package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructGetUnsignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun StructGetUnsignedDispatcher(
    instruction: AggregateInstruction.StructGetUnsigned,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    StructGetUnsignedExecutor(vstack, context, instruction)
    nextIp
}
