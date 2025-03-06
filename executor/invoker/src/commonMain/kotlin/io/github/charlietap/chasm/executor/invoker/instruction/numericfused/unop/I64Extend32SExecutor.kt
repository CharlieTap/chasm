package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64Extend32SExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Extend32S,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack)
    val result = ((operand and 0xFFFFFFFFL) shl 32) shr 32

    instruction.destination(result, stack)
}
