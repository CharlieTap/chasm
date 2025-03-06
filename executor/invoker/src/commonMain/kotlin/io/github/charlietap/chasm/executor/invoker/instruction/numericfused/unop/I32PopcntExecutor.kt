package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32PopcntExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Popcnt,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack).toInt()
    val result = operand.countOneBits().toLong()

    instruction.destination(result, stack)
}
