package io.github.charlietap.chasm.executor.invoker.instruction.referencefused

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedReferenceInstruction

internal inline fun RefNullExecutor(
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefNull,
) {
    instruction.destination(instruction.reference, context.vstack)
}
