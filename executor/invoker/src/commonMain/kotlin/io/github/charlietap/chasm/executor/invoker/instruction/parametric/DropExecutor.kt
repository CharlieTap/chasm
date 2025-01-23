package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ParametricInstruction

@Suppress("UNUSED_PARAMETER")
internal inline fun DropExecutor(
    context: ExecutionContext,
    instruction: ParametricInstruction.Drop,
) {
    context.vstack.pop()
}
