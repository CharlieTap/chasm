package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpOnCastExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun JumpOnCastDispatcher(
    instruction: AdminInstruction.JumpOnCast,
) = JumpOnCastDispatcher(
    instruction = instruction,
    executor = ::JumpOnCastExecutor,
)

internal inline fun JumpOnCastDispatcher(
    instruction: AdminInstruction.JumpOnCast,
    crossinline executor: Executor<AdminInstruction.JumpOnCast>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
