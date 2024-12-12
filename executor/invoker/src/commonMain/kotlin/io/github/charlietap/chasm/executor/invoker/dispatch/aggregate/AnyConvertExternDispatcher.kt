package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.AnyConvertExternExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

fun AnyConvertExternDispatcher(
    instruction: AggregateInstruction.AnyConvertExtern,
) = AnyConvertExternDispatcher(
    instruction = instruction,
    executor = ::AnyConvertExternExecutor,
)

internal inline fun AnyConvertExternDispatcher(
    instruction: AggregateInstruction.AnyConvertExtern,
    crossinline executor: Executor<AggregateInstruction.AnyConvertExtern>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
