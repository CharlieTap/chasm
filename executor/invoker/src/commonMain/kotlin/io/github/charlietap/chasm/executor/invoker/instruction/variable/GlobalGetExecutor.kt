package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

internal inline fun GlobalGetExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.GlobalGet,
) {
    context.vstack.push(instruction.global.value)
}
