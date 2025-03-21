package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.IfExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun IfDispatcher(
    instruction: ControlInstruction.If,
) = IfDispatcher(
    instruction = instruction,
    executor = ::IfExecutor,
)

internal inline fun IfDispatcher(
    instruction: ControlInstruction.If,
    crossinline executor: Executor<ControlInstruction.If>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
