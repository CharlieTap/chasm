package io.github.charlietap.chasm.executor.invoker.instruction.referencefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun RefIsNullExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefIsNull,
): InstructionPointer {
    val value = instruction.value(vstack)

    if (value.isNullableReference()) {
        instruction.destination(1L, vstack)
    } else {
        instruction.destination(0L, vstack)
    }
    return ip + 1
}
