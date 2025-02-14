package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

internal inline fun I32AddExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Add,
) {
    val stack = context.vstack
    instruction.destination(instruction.left(stack) + instruction.right(stack), stack)
}
