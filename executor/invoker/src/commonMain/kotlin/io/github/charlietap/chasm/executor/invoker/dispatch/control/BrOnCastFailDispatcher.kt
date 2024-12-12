package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.BrOnCastFailExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun BrOnCastFailDispatcher(
    instruction: ControlInstruction.BrOnCastFail,
) = BrOnCastFailDispatcher(
    instruction = instruction,
    executor = ::BrOnCastFailExecutor,
)

internal inline fun BrOnCastFailDispatcher(
    instruction: ControlInstruction.BrOnCastFail,
    crossinline executor: Executor<ControlInstruction.BrOnCastFail>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
