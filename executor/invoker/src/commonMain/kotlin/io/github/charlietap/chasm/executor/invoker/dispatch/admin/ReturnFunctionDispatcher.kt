package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.ReturnFunctionExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun ReturnFunctionDispatcher(
    instruction: AdminInstruction.ReturnFunction,
) = ReturnFunctionDispatcher(
    instruction = instruction,
    executor = ::ReturnFunctionExecutor,
)

internal inline fun ReturnFunctionDispatcher(
    instruction: AdminInstruction.ReturnFunction,
    crossinline executor: Executor<AdminInstruction.ReturnFunction>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
