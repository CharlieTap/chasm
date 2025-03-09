package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun ReturnDispatcher(
    instruction: ControlInstruction.Return,
) = ReturnDispatcher(
    instruction = instruction,
    executor = ::ReturnExecutor,
)

internal inline fun ReturnDispatcher(
    instruction: ControlInstruction.Return,
    crossinline executor: Executor<ControlInstruction.Return>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
