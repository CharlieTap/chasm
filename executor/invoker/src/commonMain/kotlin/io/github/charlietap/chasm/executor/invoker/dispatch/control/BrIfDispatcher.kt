package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.BrIfExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun BrIfDispatcher(
    instruction: ControlInstruction.BrIf,
) = BrIfDispatcher(
    instruction = instruction,
    executor = ::BrIfExecutor,
)

internal inline fun BrIfDispatcher(
    instruction: ControlInstruction.BrIf,
    crossinline executor: Executor<ControlInstruction.BrIf>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
