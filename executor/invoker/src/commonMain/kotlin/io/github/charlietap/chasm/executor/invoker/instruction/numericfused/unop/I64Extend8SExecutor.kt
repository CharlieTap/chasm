package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64Extend8SExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Extend8S,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack)
    val result = ((operand and 0xFFL) shl 56) shr 56

    instruction.destination(result, stack)
}
