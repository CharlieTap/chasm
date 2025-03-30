package io.github.charlietap.chasm.executor.invoker.dispatch.parametric

import io.github.charlietap.chasm.executor.invoker.instruction.parametric.SelectExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction

fun SelectDispatcher(
    instruction: ParametricInstruction.Select,
) = SelectDispatcher(
    instruction = instruction,
    executor = ::SelectExecutor,
)

internal inline fun SelectDispatcher(
    instruction: ParametricInstruction.Select,
    crossinline executor: Executor<ParametricInstruction.Select>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
