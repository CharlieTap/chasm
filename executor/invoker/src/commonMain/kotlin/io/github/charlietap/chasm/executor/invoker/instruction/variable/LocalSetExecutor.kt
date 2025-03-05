package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

internal inline fun LocalSetExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.LocalSet,
) {
    context.vstack.setLocal(
        instruction.localIdx,
        context.vstack.pop(),
    )
}
