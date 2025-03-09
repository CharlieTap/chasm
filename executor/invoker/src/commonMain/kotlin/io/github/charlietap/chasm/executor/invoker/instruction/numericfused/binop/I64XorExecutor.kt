package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64XorExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Xor,
) {
    val stack = context.vstack

    val left = instruction.left(stack)
    val right = instruction.right(stack)
    val result = left xor right

    instruction.destination(result, stack)
}
