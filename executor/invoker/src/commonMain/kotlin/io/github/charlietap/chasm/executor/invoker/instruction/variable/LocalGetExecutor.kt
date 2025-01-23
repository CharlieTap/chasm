package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

internal inline fun LocalGetExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.LocalGet,
) {
    context.vstack.push(
        context.vstack.getLocal(instruction.localIdx),
    )
}
