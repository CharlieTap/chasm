package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun RefNullExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefNull,
) {
    val stack = context.vstack
    stack.push(ReferenceValue.Null(instruction.type))
}
