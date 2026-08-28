package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ExternConvertAnyExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ExternConvertAnyDispatcher(
    instruction: AggregateInstruction.ExternConvertAny,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ExternConvertAnyExecutor(vstack, context, instruction)
    nextIp
}
