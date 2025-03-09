package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32RotrExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Rotr,
) {
    val stack = context.vstack

    val left = instruction.left(stack).toInt()
    val right = instruction.right(stack).toInt()
    val result = left.rotateRight(right)

    instruction.destination(result.toLong(), stack)
}
