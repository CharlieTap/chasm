package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.invoker.ext.max
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F64MaxExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64Max,
) {
    val stack = context.vstack

    val left = Double.fromBits(instruction.left(stack))
    val right = Double.fromBits(instruction.right(stack))

    val result = left.max(right).toRawBits()

    instruction.destination(result, stack)
}
