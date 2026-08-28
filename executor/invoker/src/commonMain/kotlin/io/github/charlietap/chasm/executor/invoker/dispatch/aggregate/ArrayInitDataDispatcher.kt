package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayInitDataExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayInitDataDispatcher(
    instruction: AggregateInstruction.ArrayInitData,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    ArrayInitDataExecutor(vstack, context, instruction)
    nextIp
}
