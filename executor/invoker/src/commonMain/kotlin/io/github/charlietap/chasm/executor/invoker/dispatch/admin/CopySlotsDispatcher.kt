package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.CopySlotsInstructionExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun CopySlotsDispatcher(
    instruction: AdminInstruction.CopySlots,
) = CopySlotsDispatcher(
    instruction = instruction,
    executor = ::CopySlotsInstructionExecutor,
)

internal inline fun CopySlotsDispatcher(
    instruction: AdminInstruction.CopySlots,
    crossinline executor: Executor<AdminInstruction.CopySlots>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
