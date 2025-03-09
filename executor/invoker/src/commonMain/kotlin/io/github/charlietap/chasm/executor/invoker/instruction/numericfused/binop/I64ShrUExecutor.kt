package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.invoker.ext.shru
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64ShrUExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ShrU,
) {
    val stack = context.vstack

    val left = instruction.left(stack)
    val right = instruction.right(stack)
    val result = left.shru(right)

    instruction.destination(result, stack)
}
