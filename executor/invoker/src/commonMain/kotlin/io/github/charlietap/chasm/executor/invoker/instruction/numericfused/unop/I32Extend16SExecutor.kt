package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32Extend16SExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Extend16S,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack).toInt()
    val result = ((operand and 0xFFFF) shl 16) shr 16

    instruction.destination(result.toLong(), stack)
}
