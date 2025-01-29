package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

internal inline fun GlobalSetExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.GlobalSet,
) {
    instruction.global.value = context.vstack.pop()
}
