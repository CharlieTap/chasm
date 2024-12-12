package io.github.charlietap.chasm.executor.invoker.dispatch.parametric

import io.github.charlietap.chasm.executor.invoker.instruction.parametric.SelectWithTypeExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ParametricInstruction

fun SelectWithTypeDispatcher(
    instruction: ParametricInstruction.SelectWithType,
) = SelectWithTypeDispatcher(
    instruction = instruction,
    executor = ::SelectWithTypeExecutor,
)

internal inline fun SelectWithTypeDispatcher(
    instruction: ParametricInstruction.SelectWithType,
    crossinline executor: Executor<ParametricInstruction.SelectWithType>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
