package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpTableExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun JumpTableDispatcher(
    instruction: AdminInstruction.JumpTable,
) = JumpTableDispatcher(
    instruction = instruction,
    executor = ::JumpTableExecutor,
)

internal inline fun JumpTableDispatcher(
    instruction: AdminInstruction.JumpTable,
    crossinline executor: Executor<AdminInstruction.JumpTable>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
