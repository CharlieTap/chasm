package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayGetUnsignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayGetUnsignedDispatcher(
    instruction: AggregateInstruction.ArrayGetUnsigned,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArrayGetUnsignedExecutor(vstack, context, instruction)
    nextIp
}
