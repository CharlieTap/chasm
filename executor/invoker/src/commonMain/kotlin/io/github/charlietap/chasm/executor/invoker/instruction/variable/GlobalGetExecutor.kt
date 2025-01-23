package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

internal inline fun GlobalGetExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.GlobalGet,
) {
    val value = instruction.global.value
    context.vstack.push(value)
}
