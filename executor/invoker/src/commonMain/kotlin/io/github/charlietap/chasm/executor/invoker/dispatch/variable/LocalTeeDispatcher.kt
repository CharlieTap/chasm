package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.executor.invoker.instruction.variable.LocalTeeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

fun LocalTeeDispatcher(
    instruction: VariableInstruction.LocalTee,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    LocalTeeExecutor(vstack, context, instruction)
    nextIp
}
