package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.BrOnNullExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun BrOnNullDispatcher(
    instruction: ControlInstruction.BrOnNull,
) = BrOnNullDispatcher(
    instruction = instruction,
    executor = ::BrOnNullExecutor,
)

internal inline fun BrOnNullDispatcher(
    instruction: ControlInstruction.BrOnNull,
    crossinline executor: Executor<ControlInstruction.BrOnNull>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
