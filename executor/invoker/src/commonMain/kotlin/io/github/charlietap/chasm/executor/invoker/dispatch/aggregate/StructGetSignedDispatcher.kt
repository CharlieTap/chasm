package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructGetSignedExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun StructGetSignedDispatcher(
    instruction: AggregateInstruction.StructGetSigned,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    StructGetSignedExecutor(vstack, context, instruction)
    nextIp
}
