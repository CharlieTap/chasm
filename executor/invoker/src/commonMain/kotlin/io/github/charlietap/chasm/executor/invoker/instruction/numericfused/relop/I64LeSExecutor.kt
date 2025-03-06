package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64LeSExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64LeS,
) {
    val stack = context.vstack
    val left = instruction.left(stack)
    val right = instruction.right(stack)

    instruction.destination(if (left <= right) 1L else 0L, stack)
}
