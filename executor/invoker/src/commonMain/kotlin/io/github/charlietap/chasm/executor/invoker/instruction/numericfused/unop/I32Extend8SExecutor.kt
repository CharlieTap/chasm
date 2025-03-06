package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32Extend8SExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Extend8S,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack).toInt()
    val result = ((operand and 0xFF) shl 24) shr 24

    instruction.destination(result.toLong(), stack)
}
