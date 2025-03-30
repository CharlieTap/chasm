package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpIfNotExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun JumpIfNotDispatcher(
    instruction: AdminInstruction.JumpIfNot,
) = JumpIfNotDispatcher(
    instruction = instruction,
    executor = ::JumpIfNotExecutor,
)

internal inline fun JumpIfNotDispatcher(
    instruction: AdminInstruction.JumpIfNot,
    crossinline executor: Executor<AdminInstruction.JumpIfNot>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
