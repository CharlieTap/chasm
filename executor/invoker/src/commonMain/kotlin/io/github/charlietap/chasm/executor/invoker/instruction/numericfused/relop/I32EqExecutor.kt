package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

internal inline fun I32EqExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Eq,
) {
    val stack = context.vstack
    val result = if (instruction.left(stack) == instruction.right(stack)) 1L else 0L
    instruction.destination(result, stack)
}
