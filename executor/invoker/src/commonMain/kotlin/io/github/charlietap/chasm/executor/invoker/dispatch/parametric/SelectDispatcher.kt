package io.github.charlietap.chasm.executor.invoker.dispatch.parametric

import io.github.charlietap.chasm.executor.invoker.instruction.parametric.SelectExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ParametricInstruction

fun SelectDispatcher(
    instruction: ParametricInstruction.Select,
) = SelectDispatcher(
    instruction = instruction,
    executor = ::SelectExecutor,
)

internal inline fun SelectDispatcher(
    instruction: ParametricInstruction.Select,
    crossinline executor: Executor<ParametricInstruction.Select>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
