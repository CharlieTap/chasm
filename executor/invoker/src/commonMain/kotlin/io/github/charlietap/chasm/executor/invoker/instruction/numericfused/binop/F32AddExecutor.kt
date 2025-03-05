package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F32AddExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32Add,
) {
    val stack = context.vstack

    val left = Float.fromBits(instruction.left(stack).toInt())
    val right = Float.fromBits(instruction.right(stack).toInt())

    val result = (left + right).toRawBits()

    instruction.destination(result.toLong(), stack)
}
