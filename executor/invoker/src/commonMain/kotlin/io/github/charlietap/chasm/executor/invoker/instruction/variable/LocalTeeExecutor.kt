package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

internal inline fun LocalTeeExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.LocalTee,
) {
    context.stack.setLocal(
        instruction.localIdx,
        context.stack.peekValue(),
    )
}
