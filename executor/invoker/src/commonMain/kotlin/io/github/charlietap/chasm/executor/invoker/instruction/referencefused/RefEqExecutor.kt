package io.github.charlietap.chasm.executor.invoker.instruction.referencefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun RefEqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefEq,
) {
    val reference1 = instruction.reference1(vstack)
    val reference2 = instruction.reference2(vstack)

    val bothTypesAreNull = reference1.isNullableReference() && reference2.isNullableReference()
    if (bothTypesAreNull || reference1 == reference2) {
        instruction.destination(1L, vstack)
    } else {
        instruction.destination(0L, vstack)
    }
}
