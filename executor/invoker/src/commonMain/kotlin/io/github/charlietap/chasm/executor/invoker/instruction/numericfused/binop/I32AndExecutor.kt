package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32AndExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32And,
) {
    val stack = context.vstack
    instruction.destination(instruction.left(stack) and instruction.right(stack), stack)
}
