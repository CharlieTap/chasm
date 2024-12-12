package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.IfExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun IfDispatcher(
    instruction: ControlInstruction.If,
) = IfDispatcher(
    instruction = instruction,
    executor = ::IfExecutor,
)

internal inline fun IfDispatcher(
    instruction: ControlInstruction.If,
    crossinline executor: Executor<ControlInstruction.If>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
