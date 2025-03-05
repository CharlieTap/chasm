package io.github.charlietap.chasm.executor.invoker.dispatch.variablefused

import io.github.charlietap.chasm.executor.invoker.instruction.variablefused.LocalSetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedVariableInstruction

fun LocalSetDispatcher(
    instruction: FusedVariableInstruction.LocalSet,
) = LocalSetDispatcher(
    instruction = instruction,
    executor = ::LocalSetExecutor,
)

internal inline fun LocalSetDispatcher(
    instruction: FusedVariableInstruction.LocalSet,
    crossinline executor: Executor<FusedVariableInstruction.LocalSet>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
