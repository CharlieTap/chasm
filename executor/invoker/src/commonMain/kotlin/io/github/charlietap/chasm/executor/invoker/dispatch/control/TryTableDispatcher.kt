package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.TryTableExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun TryTableDispatcher(
    instruction: ControlInstruction.TryTable,
) = TryTableDispatcher(
    instruction = instruction,
    executor = ::TryTableExecutor,
)

internal inline fun TryTableDispatcher(
    instruction: ControlInstruction.TryTable,
    crossinline executor: Executor<ControlInstruction.TryTable>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
