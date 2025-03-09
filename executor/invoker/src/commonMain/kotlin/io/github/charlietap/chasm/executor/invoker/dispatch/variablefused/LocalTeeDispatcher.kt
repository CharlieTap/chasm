package io.github.charlietap.chasm.executor.invoker.dispatch.variablefused

import io.github.charlietap.chasm.executor.invoker.instruction.variablefused.LocalTeeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedVariableInstruction

fun LocalTeeDispatcher(
    instruction: FusedVariableInstruction.LocalTee,
) = LocalTeeDispatcher(
    instruction = instruction,
    executor = ::LocalTeeExecutor,
)

internal inline fun LocalTeeDispatcher(
    instruction: FusedVariableInstruction.LocalTee,
    crossinline executor: Executor<FusedVariableInstruction.LocalTee>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
