package io.github.charlietap.chasm.executor.invoker.instruction.referencefused

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.isNullableReference
import io.github.charlietap.chasm.executor.runtime.instruction.FusedReferenceInstruction

internal inline fun RefIsNullExecutor(
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefIsNull,
) {
    val stack = context.vstack
    val value = instruction.value(stack)

    if (value.isNullableReference()) {
        instruction.destination(1L, stack)
    } else {
        instruction.destination(0L, stack)
    }
}
