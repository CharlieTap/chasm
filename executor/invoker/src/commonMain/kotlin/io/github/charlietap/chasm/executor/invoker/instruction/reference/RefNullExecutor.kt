package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun RefNullExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefNull,
) {
    vstack.push(instruction.reference)
}
