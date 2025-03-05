package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

internal inline fun RefEqExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefEq,
) {
    val stack = context.vstack

    val reference1 = stack.pop()
    val reference2 = stack.pop()

    val bothTypesAreNull = reference1.isNullableReference() && reference2.isNullableReference()
    if (bothTypesAreNull || reference1 == reference2) {
        stack.push(1L)
    } else {
        stack.push(0L)
    }
}
