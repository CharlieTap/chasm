package io.github.charlietap.chasm.executor.invoker.instruction.variablefused

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedVariableInstruction

internal inline fun LocalSetExecutor(
    context: ExecutionContext,
    instruction: FusedVariableInstruction.LocalSet,
) {
    context.vstack.setLocal(
        instruction.localIdx,
        instruction.operand(context.vstack),
    )
}
