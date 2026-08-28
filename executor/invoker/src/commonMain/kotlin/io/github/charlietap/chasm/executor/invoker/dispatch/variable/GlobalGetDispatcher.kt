package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.executor.invoker.instruction.variable.GlobalGetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

fun GlobalGetDispatcher(
    instruction: VariableInstruction.GlobalGet,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    GlobalGetExecutor(vstack, context, instruction)
    nextIp
}
