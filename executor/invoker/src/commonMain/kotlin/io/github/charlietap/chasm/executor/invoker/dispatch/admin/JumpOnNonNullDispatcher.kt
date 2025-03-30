package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpOnNonNullExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun JumpOnNonNullDispatcher(
    instruction: AdminInstruction.JumpOnNonNull,
) = JumpOnNonNullDispatcher(
    instruction = instruction,
    executor = ::JumpOnNonNullExecutor,
)

internal inline fun JumpOnNonNullDispatcher(
    instruction: AdminInstruction.JumpOnNonNull,
    crossinline executor: Executor<AdminInstruction.JumpOnNonNull>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
