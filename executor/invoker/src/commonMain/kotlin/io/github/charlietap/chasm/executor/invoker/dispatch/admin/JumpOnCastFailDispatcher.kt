package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.JumpOnCastFailExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun JumpOnCastFailDispatcher(
    instruction: AdminInstruction.JumpOnCastFail,
) = JumpOnCastFailDispatcher(
    instruction = instruction,
    executor = ::JumpOnCastFailExecutor,
)

internal inline fun JumpOnCastFailDispatcher(
    instruction: AdminInstruction.JumpOnCastFail,
    crossinline executor: Executor<AdminInstruction.JumpOnCastFail>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
