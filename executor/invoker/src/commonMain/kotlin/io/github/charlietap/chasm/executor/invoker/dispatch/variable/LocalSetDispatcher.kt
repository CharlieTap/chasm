package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.executor.invoker.instruction.variable.LocalSetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

fun LocalSetDispatcher(
    instruction: VariableInstruction.LocalSet,
) = LocalSetDispatcher(
    instruction = instruction,
    executor = ::LocalSetExecutor,
)

internal inline fun LocalSetDispatcher(
    instruction: VariableInstruction.LocalSet,
    crossinline executor: Executor<VariableInstruction.LocalSet>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
