package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.executor.invoker.instruction.variable.LocalSetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

fun LocalSetDispatcher(
    instruction: VariableInstruction.LocalSet,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    LocalSetExecutor(vstack, context, instruction)
    nextIp
}
