package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayGetSignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayGetSignedDispatcher(
    instruction: AggregateInstruction.ArrayGetSigned,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArrayGetSignedExecutor(vstack, context, instruction)
    nextIp
}
