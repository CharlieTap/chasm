package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.EndFunctionInstructionExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun EndFunctionDispatcher(
    instruction: AdminInstruction.EndFunction,
) = EndFunctionDispatcher(
    instruction = instruction,
    executor = ::EndFunctionInstructionExecutor,
)

internal inline fun EndFunctionDispatcher(
    instruction: AdminInstruction.EndFunction,
    crossinline executor: Executor<AdminInstruction.EndFunction>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
