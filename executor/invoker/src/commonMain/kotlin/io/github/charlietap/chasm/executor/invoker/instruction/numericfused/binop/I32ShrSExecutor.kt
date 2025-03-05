package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32ShrSExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32ShrS,
) {
    val stack = context.vstack
    val result = instruction.left(stack).toInt() shr instruction.right(stack).toInt()
    instruction.destination(result.toLong(), stack)
}
