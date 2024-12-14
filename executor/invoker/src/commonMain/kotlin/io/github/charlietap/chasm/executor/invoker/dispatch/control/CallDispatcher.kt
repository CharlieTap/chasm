package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.CallExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun CallDispatcher(
    instruction: ControlInstruction.Call,
) = CallDispatcher(
    instruction = instruction,
    executor = ::CallExecutor,
)

internal inline fun CallDispatcher(
    instruction: ControlInstruction.Call,
    crossinline executor: Executor<ControlInstruction.Call>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
