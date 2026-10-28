package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun EndFunctionDispatcher(
    instruction: AdminInstruction.EndFunction,
): DispatchableInstruction = DispatchableInstruction { vstack, context, _ ->
    ReturnExecutor(vstack, context, instruction.resultCount, instruction.activationHeaderSlot)
}
