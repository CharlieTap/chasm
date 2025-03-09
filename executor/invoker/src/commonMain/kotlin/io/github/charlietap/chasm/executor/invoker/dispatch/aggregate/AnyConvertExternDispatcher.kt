package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.AnyConvertExternExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun AnyConvertExternDispatcher(
    instruction: AggregateInstruction.AnyConvertExtern,
) = AnyConvertExternDispatcher(
    instruction = instruction,
    executor = ::AnyConvertExternExecutor,
)

internal inline fun AnyConvertExternDispatcher(
    instruction: AggregateInstruction.AnyConvertExtern,
    crossinline executor: Executor<AggregateInstruction.AnyConvertExtern>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
