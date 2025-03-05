package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

internal inline fun RefNullExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefNull,
) {
    context.vstack.push(instruction.reference)
}
