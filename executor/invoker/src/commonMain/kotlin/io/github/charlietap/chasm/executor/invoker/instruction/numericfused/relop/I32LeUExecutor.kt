package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32LeUExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32LeU,
) {
    val stack = context.vstack
    val result = if (instruction.left(stack).toULong() <= instruction.right(stack).toULong()) 1L else 0L
    instruction.destination(result, stack)
}
