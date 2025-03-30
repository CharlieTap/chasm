package io.github.charlietap.chasm.executor.invoker.dispatch.parametric

import io.github.charlietap.chasm.executor.invoker.instruction.parametric.SelectWithTypeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction

fun SelectWithTypeDispatcher(
    instruction: ParametricInstruction.SelectWithType,
) = SelectWithTypeDispatcher(
    instruction = instruction,
    executor = ::SelectWithTypeExecutor,
)

internal inline fun SelectWithTypeDispatcher(
    instruction: ParametricInstruction.SelectWithType,
    crossinline executor: Executor<ParametricInstruction.SelectWithType>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
