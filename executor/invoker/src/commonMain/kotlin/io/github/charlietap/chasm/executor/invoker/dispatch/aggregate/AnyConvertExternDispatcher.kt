package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.AnyConvertExternExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun AnyConvertExternDispatcher(
    instruction: AggregateInstruction.AnyConvertExtern,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    AnyConvertExternExecutor(vstack, context, instruction)
    nextIp
}
