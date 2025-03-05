package io.github.charlietap.chasm.executor.invoker.dispatch.parametric

import io.github.charlietap.chasm.executor.invoker.instruction.parametric.DropExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction

fun DropDispatcher(
    instruction: ParametricInstruction.Drop,
) = DropDispatcher(
    instruction = instruction,
    executor = ::DropExecutor,
)

internal inline fun DropDispatcher(
    instruction: ParametricInstruction.Drop,
    crossinline executor: Executor<ParametricInstruction.Drop>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
