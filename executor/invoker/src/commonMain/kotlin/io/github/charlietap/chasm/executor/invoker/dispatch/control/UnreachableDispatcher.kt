package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.UnreachableExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun UnreachableDispatcher(
    instruction: ControlInstruction.Unreachable,
) = UnreachableDispatcher(
    instruction = instruction,
    executor = ::UnreachableExecutor,
)

internal inline fun UnreachableDispatcher(
    instruction: ControlInstruction.Unreachable,
    crossinline executor: Executor<ControlInstruction.Unreachable>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
