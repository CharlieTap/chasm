package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun RefIsNullExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefIsNull,
) {
    val value = vstack.pop()

    if (value.isNullableReference()) {
        vstack.push(1L)
    } else {
        vstack.push(0L)
    }
}
