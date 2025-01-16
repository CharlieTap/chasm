package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun RefAsNonNullExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefAsNonNull,
) {

    val (stack) = context

    val value = stack.peekValue()

    if (value is ReferenceValue) {
        if (value is ReferenceValue.Null) {
            Err(InvocationError.Trap.TrapEncountered).bind()
        }
    } else {
        Err(InvocationError.ReferenceValueExpected).bind()
    }
}
