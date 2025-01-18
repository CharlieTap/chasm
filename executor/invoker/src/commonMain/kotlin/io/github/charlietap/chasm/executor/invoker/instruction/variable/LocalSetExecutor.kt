package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

internal inline fun LocalSetExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.LocalSet,
) {
    context.stack.setLocal(
        instruction.localIdx,
        context.stack.popValue(),
    )
}
