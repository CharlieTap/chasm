package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

internal inline fun LocalTeeExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.LocalTee,
) {
    context.vstack.setLocal(
        instruction.localIdx,
        context.vstack.peek(),
    )
}
