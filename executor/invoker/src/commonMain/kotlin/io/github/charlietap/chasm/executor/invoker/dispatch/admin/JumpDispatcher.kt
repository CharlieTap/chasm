package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun JumpDispatcher(
    instruction: AdminInstruction.Jump,
) = JumpDispatcher(
    instruction = instruction,
    executor = ::JumpExecutor,
)

internal inline fun JumpDispatcher(
    instruction: AdminInstruction.Jump,
    crossinline executor: Executor<AdminInstruction.Jump>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
