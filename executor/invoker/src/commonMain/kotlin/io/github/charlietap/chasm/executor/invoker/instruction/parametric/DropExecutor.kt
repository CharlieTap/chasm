package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

@Suppress("UNUSED_PARAMETER")
internal inline fun DropExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ParametricInstruction.Drop,
) {
    vstack.pop()
}
