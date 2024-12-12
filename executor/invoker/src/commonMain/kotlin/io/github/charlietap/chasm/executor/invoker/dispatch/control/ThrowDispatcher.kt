package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.ThrowExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun ThrowDispatcher(
    instruction: ControlInstruction.Throw,
) = ThrowDispatcher(
    instruction = instruction,
    executor = ::ThrowExecutor,
)

internal inline fun ThrowDispatcher(
    instruction: ControlInstruction.Throw,
    crossinline executor: Executor<ControlInstruction.Throw>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
