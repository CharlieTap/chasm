package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.executor.invoker.instruction.variable.GlobalGetExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

fun GlobalGetDispatcher(
    instruction: VariableInstruction.GlobalGet,
) = GlobalGetDispatcher(
    instruction = instruction,
    executor = ::GlobalGetExecutor,
)

internal inline fun GlobalGetDispatcher(
    instruction: VariableInstruction.GlobalGet,
    crossinline executor: Executor<VariableInstruction.GlobalGet>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
