package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.executor.invoker.instruction.variable.LocalTeeExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

fun LocalTeeDispatcher(
    instruction: VariableInstruction.LocalTee,
) = LocalTeeDispatcher(
    instruction = instruction,
    executor = ::LocalTeeExecutor,
)

internal inline fun LocalTeeDispatcher(
    instruction: VariableInstruction.LocalTee,
    crossinline executor: Executor<VariableInstruction.LocalTee>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
