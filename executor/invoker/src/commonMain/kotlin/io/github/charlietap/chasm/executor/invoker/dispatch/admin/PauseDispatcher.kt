package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.PauseInstructionExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun PauseDispatcher(
    instruction: AdminInstruction.Pause,
) = PauseDispatcher(
    instruction = instruction,
    executor = ::PauseInstructionExecutor,
)

internal inline fun PauseDispatcher(
    instruction: AdminInstruction.Pause,
    crossinline executor: Executor<AdminInstruction.Pause>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
