package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpAdjustingExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun JumpAdjustingDispatcher(
    instruction: AdminInstruction.JumpAdjusting,
) = JumpAdjustingDispatcher(
    instruction = instruction,
    executor = ::JumpAdjustingExecutor,
)

internal inline fun JumpAdjustingDispatcher(
    instruction: AdminInstruction.JumpAdjusting,
    crossinline executor: Executor<AdminInstruction.JumpAdjusting>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
