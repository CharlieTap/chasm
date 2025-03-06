package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32CtzExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Ctz,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack).toInt()
    val result = operand.countTrailingZeroBits().toLong()

    instruction.destination(result, stack)
}
