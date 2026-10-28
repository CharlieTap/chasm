package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction

fun EndFunctionDispatcher(
    resultCount: Int,
    activationHeaderSlot: Int,
): DispatchableInstruction = DispatchableInstruction { vstack, context, _ ->
    ReturnExecutor(vstack, context, resultCount, activationHeaderSlot)
}
