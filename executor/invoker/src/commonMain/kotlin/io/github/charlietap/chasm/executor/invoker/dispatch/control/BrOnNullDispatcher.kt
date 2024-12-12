package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.BrOnNullExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun BrOnNullDispatcher(
    instruction: ControlInstruction.BrOnNull,
) = BrOnNullDispatcher(
    instruction = instruction,
    executor = ::BrOnNullExecutor,
)

internal inline fun BrOnNullDispatcher(
    instruction: ControlInstruction.BrOnNull,
    crossinline executor: Executor<ControlInstruction.BrOnNull>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
