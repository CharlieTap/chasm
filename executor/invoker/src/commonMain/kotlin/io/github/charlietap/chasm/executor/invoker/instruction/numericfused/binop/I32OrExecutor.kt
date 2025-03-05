package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32OrExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Or,
) {
    val stack = context.vstack
    instruction.destination(instruction.left(stack) or instruction.right(stack), stack)
}
