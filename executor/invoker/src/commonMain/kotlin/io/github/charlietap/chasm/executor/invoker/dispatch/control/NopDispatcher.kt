package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.NopExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun NopDispatcher(
    instruction: ControlInstruction.Nop,
) = NopDispatcher(
    instruction = instruction,
    executor = ::NopExecutor,
)

internal inline fun NopDispatcher(
    instruction: ControlInstruction.Nop,
    crossinline executor: Executor<ControlInstruction.Nop>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
