package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64Extend16SExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Extend16S,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack)
    val result = ((operand and 0xFFFFL) shl 48) shr 48

    instruction.destination(result, stack)
}
