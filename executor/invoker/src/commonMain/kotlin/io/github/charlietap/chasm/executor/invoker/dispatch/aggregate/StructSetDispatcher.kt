package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructSetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun StructSetDispatcher(
    instruction: AggregateInstruction.StructSet,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    StructSetExecutor(vstack, context, instruction)
    nextIp
}
