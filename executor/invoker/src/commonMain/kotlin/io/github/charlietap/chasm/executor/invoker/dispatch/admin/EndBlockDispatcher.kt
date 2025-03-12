package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.EndBlockInstructionExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun EndBlockDispatcher(
    instruction: AdminInstruction.EndBlock,
) = EndBlockDispatcher(
    instruction = instruction,
    executor = ::EndBlockInstructionExecutor,
)

internal inline fun EndBlockDispatcher(
    instruction: AdminInstruction.EndBlock,
    crossinline executor: Executor<AdminInstruction.EndBlock>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
