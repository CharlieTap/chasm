package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.CallRefExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun CallRefDispatcher(
    instruction: ControlInstruction.CallRef,
) = CallRefDispatcher(
    instruction = instruction,
    executor = ::CallRefExecutor,
)

internal inline fun CallRefDispatcher(
    instruction: ControlInstruction.CallRef,
    crossinline executor: Executor<ControlInstruction.CallRef>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
