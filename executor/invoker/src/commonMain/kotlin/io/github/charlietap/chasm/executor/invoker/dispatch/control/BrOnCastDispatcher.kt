package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.BrOnCastExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun BrOnCastDispatcher(
    instruction: ControlInstruction.BrOnCast,
) = BrOnCastDispatcher(
    instruction = instruction,
    executor = ::BrOnCastExecutor,
)

internal inline fun BrOnCastDispatcher(
    instruction: ControlInstruction.BrOnCast,
    crossinline executor: Executor<ControlInstruction.BrOnCast>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}