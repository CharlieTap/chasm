package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun RefEqExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefEq,
): InstructionPointer {
    val reference1 = vstack.pop()
    val reference2 = vstack.pop()

    val bothTypesAreNull = reference1.isNullableReference() && reference2.isNullableReference()
    if (bothTypesAreNull || reference1 == reference2) {
        vstack.push(1L)
    } else {
        vstack.push(0L)
    }

    return ip + 1
}
