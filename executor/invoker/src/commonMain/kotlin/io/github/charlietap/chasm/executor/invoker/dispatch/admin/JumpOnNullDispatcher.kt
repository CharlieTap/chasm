package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpOnNullExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun JumpOnNullDispatcher(
    instruction: AdminInstruction.JumpOnNull,
) = JumpOnNullDispatcher(
    instruction = instruction,
    executor = ::JumpOnNullExecutor,
)

internal inline fun JumpOnNullDispatcher(
    instruction: AdminInstruction.JumpOnNull,
    crossinline executor: Executor<AdminInstruction.JumpOnNull>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
