package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.executor.invoker.instruction.variable.LocalGetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

fun LocalGetDispatcher(
    instruction: VariableInstruction.LocalGet,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    LocalGetExecutor(vstack, context, instruction)
    nextIp
}
