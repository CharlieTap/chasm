package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.executor.invoker.instruction.variable.GlobalSetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

fun GlobalSetDispatcher(
    instruction: VariableInstruction.GlobalSet,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    GlobalSetExecutor(vstack, context, instruction)
    nextIp
}
