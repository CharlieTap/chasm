package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.RefI31Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun RefI31Dispatcher(
    instruction: AggregateInstruction.RefI31,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    RefI31Executor(vstack, context, instruction)
    nextIp
}
