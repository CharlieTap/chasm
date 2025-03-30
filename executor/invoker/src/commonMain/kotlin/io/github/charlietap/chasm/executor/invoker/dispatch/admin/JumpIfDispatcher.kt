package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpIfExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun JumpIfDispatcher(
    instruction: AdminInstruction.JumpIf,
) = JumpIfDispatcher(
    instruction = instruction,
    executor = ::JumpIfExecutor,
)

internal inline fun JumpIfDispatcher(
    instruction: AdminInstruction.JumpIf,
    crossinline executor: Executor<AdminInstruction.JumpIf>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
