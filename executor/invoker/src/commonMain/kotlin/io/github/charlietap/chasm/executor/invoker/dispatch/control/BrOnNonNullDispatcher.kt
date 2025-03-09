package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.BrOnNonNullExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun BrOnNonNullDispatcher(
    instruction: ControlInstruction.BrOnNonNull,
) = BrOnNonNullDispatcher(
    instruction = instruction,
    executor = ::BrOnNonNullExecutor,
)

internal inline fun BrOnNonNullDispatcher(
    instruction: ControlInstruction.BrOnNonNull,
    crossinline executor: Executor<ControlInstruction.BrOnNonNull>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
