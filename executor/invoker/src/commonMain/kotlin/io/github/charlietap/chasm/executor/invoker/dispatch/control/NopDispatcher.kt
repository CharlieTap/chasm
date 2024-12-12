package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.NopExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun NopDispatcher(
    instruction: ControlInstruction.Nop,
) = NopDispatcher(
    instruction = instruction,
    executor = ::NopExecutor,
)

internal inline fun NopDispatcher(
    instruction: ControlInstruction.Nop,
    crossinline executor: Executor<ControlInstruction.Nop>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
