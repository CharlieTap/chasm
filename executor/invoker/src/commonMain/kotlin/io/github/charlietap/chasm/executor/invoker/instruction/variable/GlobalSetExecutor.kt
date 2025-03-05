package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

internal inline fun GlobalSetExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.GlobalSet,
) {
    instruction.global.value = context.vstack.pop()
}
