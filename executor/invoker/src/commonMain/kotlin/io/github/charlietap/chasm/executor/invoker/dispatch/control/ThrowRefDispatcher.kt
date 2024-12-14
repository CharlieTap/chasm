package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.ThrowRefExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun ThrowRefDispatcher(
    instruction: ControlInstruction.ThrowRef,
) = ThrowRefDispatcher(
    instruction = instruction,
    executor = ::ThrowRefExecutor,
)

internal inline fun ThrowRefDispatcher(
    instruction: ControlInstruction.ThrowRef,
    crossinline executor: Executor<ControlInstruction.ThrowRef>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
