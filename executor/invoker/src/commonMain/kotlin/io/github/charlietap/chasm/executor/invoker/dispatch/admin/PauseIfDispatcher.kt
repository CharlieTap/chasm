package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.PauseIfInstructionExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun PauseIfDispatcher(
    instruction: AdminInstruction.PauseIf,
) = PauseIfDispatcher(
    instruction = instruction,
    executor = ::PauseIfInstructionExecutor,
)

internal inline fun PauseIfDispatcher(
    instruction: AdminInstruction.PauseIf,
    crossinline executor: Executor<AdminInstruction.PauseIf>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
