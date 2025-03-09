package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.invoker.ext.copySign
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F32CopysignExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32Copysign,
) {
    val stack = context.vstack

    val left = Float.fromBits(instruction.left(stack).toInt())
    val right = Float.fromBits(instruction.right(stack).toInt())

    val result = left.copySign(right).toRawBits()

    instruction.destination(result.toLong(), stack)
}
