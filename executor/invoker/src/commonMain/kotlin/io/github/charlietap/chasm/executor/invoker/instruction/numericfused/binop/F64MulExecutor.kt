package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F64MulExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64Mul,
) {
    val stack = context.vstack

    val left = Double.fromBits(instruction.left(stack))
    val right = Double.fromBits(instruction.right(stack))

    val result = (left * right).toRawBits()

    instruction.destination(result, stack)
}
