package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.BrOnNonNullExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun BrOnNonNullDispatcher(
    instruction: ControlInstruction.BrOnNonNull,
) = BrOnNonNullDispatcher(
    instruction = instruction,
    executor = ::BrOnNonNullExecutor,
)

internal inline fun BrOnNonNullDispatcher(
    instruction: ControlInstruction.BrOnNonNull,
    crossinline executor: Executor<ControlInstruction.BrOnNonNull>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
