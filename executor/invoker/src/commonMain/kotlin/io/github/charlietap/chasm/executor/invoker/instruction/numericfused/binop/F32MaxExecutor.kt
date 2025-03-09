package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.invoker.ext.max
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F32MaxExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32Max,
) {
    val stack = context.vstack

    val left = Float.fromBits(instruction.left(stack).toInt())
    val right = Float.fromBits(instruction.right(stack).toInt())

    val result = left.max(right).toRawBits()

    instruction.destination(result.toLong(), stack)
}
