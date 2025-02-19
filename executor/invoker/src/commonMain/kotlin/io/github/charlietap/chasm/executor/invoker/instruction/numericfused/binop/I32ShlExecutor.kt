package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

internal inline fun I32ShlExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Shl,
) {
    val stack = context.vstack
    val result = instruction.left(stack).toInt() shl instruction.right(stack).toInt()
    instruction.destination(result.toLong(), stack)
}
