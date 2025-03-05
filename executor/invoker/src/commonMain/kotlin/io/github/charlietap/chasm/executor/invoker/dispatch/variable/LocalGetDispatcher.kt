package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.executor.invoker.instruction.variable.LocalGetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

fun LocalGetDispatcher(
    instruction: VariableInstruction.LocalGet,
) = LocalGetDispatcher(
    instruction = instruction,
    executor = ::LocalGetExecutor,
)

internal inline fun LocalGetDispatcher(
    instruction: VariableInstruction.LocalGet,
    crossinline executor: Executor<VariableInstruction.LocalGet>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
