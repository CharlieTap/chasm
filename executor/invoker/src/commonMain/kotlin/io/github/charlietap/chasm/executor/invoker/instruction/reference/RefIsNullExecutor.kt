package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

internal inline fun RefIsNullExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefIsNull,
) {
    val stack = context.vstack
    val value = stack.pop()

    if (value.isNullableReference()) {
        stack.push(1L)
    } else {
        stack.push(0L)
    }
}
