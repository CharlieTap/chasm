package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.invoker.ext.rotateRight
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64RotrExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Rotr,
) {
    val stack = context.vstack

    val left = instruction.left(stack)
    val right = instruction.right(stack)
    val result = left.rotateRight(right)

    instruction.destination(result, stack)
}
