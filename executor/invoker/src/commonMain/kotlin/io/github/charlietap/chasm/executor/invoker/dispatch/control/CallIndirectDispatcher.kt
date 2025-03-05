package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.CallIndirectExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun CallIndirectDispatcher(
    instruction: ControlInstruction.CallIndirect,
) = CallIndirectDispatcher(
    instruction = instruction,
    executor = ::CallIndirectExecutor,
)

internal inline fun CallIndirectDispatcher(
    instruction: ControlInstruction.CallIndirect,
    crossinline executor: Executor<ControlInstruction.CallIndirect>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
