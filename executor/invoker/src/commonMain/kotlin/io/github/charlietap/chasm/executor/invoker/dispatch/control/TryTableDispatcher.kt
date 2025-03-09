package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.TryTableExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun TryTableDispatcher(
    instruction: ControlInstruction.TryTable,
) = TryTableDispatcher(
    instruction = instruction,
    executor = ::TryTableExecutor,
)

internal inline fun TryTableDispatcher(
    instruction: ControlInstruction.TryTable,
    crossinline executor: Executor<ControlInstruction.TryTable>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
