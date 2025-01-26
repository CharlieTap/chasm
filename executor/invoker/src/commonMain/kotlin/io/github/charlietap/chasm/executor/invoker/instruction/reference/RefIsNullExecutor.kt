package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun RefIsNullExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefIsNull,
) {
    val stack = context.vstack
    val value = stack.popReference()

    if (value is ReferenceValue.Null) {
        stack.push(1L)
    } else {
        stack.push(0L)
    }
}
