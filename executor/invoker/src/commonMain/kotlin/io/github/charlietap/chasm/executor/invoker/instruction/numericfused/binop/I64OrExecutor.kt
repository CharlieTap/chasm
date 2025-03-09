package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64OrExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Or,
) {
    val stack = context.vstack

    val left = instruction.left(stack)
    val right = instruction.right(stack)
    val result = left or right

    instruction.destination(result, stack)
}
