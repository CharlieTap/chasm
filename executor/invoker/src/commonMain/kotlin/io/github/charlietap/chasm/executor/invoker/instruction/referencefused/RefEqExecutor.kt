package io.github.charlietap.chasm.executor.invoker.instruction.referencefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction

internal inline fun RefEqExecutor(
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefEq,
) {
    val stack = context.vstack

    val reference1 = instruction.reference1(stack)
    val reference2 = instruction.reference2(stack)

    val bothTypesAreNull = reference1.isNullableReference() && reference2.isNullableReference()
    if (bothTypesAreNull || reference1 == reference2) {
        instruction.destination(1L, stack)
    } else {
        instruction.destination(0L, stack)
    }
}
